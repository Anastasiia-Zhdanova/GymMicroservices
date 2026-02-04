package com.company.gym.dao;

import com.company.gym.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({TrainingDAO.class, TraineeDAO.class, TrainerDAO.class, UserDAO.class, TrainingTypeDAO.class})
public class TrainingDAOTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrainingDAO trainingDAO;

    private Training testTraining;
    private Long trainingId;

    @BeforeEach
    void setUp() {
        User uTrainee = new User();
        uTrainee.setUsername("trainee.test");
        uTrainee.setFirstName("Trainee");
        uTrainee.setLastName("Test");
        uTrainee.setPassword("p");

        User uTrainer = new User();
        uTrainer.setUsername("trainer.test");
        uTrainer.setFirstName("Trainer");
        uTrainer.setLastName("Test");
        uTrainer.setPassword("p");

        entityManager.persist(uTrainee);
        entityManager.persist(uTrainer);

        TrainingType type = new TrainingType("Yoga");
        entityManager.persist(type);

        Trainer trainer = new Trainer();
        trainer.setUser(uTrainer);
        trainer.setSpecialization(type);
        entityManager.persist(trainer);

        Trainee trainee = new Trainee();
        trainee.setUser(uTrainee);
        entityManager.persist(trainee);

        testTraining = new Training();
        testTraining.setTrainee(trainee);
        testTraining.setTrainer(trainer);
        testTraining.setTrainingType(type);
        testTraining.setTrainingName("Morning Yoga");
        testTraining.setTrainingDate(new Date());
        testTraining.setTrainingDuration(60);
    }

    @Test
    void save_and_findById_Success() {
        Training savedTraining = trainingDAO.save(testTraining);
        trainingId = savedTraining.getId();

        assertNotNull(trainingId);
        Training foundTraining = trainingDAO.findById(trainingId);

        assertNotNull(foundTraining);
        assertEquals("Morning Yoga", foundTraining.getTrainingName());
        assertEquals("trainee.test", foundTraining.getTrainee().getUser().getUsername());
    }
}