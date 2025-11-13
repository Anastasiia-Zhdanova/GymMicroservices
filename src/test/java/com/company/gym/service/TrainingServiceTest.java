package com.company.gym.service;

import com.company.gym.dao.TraineeDAO;
import com.company.gym.dao.TrainerDAO;
import com.company.gym.dao.TrainingDAO;
import com.company.gym.entity.*;
import com.company.gym.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest {

    @Mock
    private TrainingDAO trainingDAO;
    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private TrainerDAO trainerDAO;

    @InjectMocks
    private TrainingService trainingService;

    private Trainee mockTrainee;
    private Trainer mockTrainer;
    private TrainingType mockTrainingType;
    private final String TRAINEE_USER = "trainee.one";
    private final String TRAINER_USER = "trainer.one";
    private final String TRAINING_NAME = "Circuit";
    private final Date TRAINING_DATE = new Date();
    private final Integer TRAINING_DURATION = 90;

    @BeforeEach
    void setUp() {
        mockTrainingType = new TrainingType("Yoga");
        mockTrainingType.setId(1L);

        User trainerUser = new User();
        trainerUser.setUsername(TRAINER_USER);
        mockTrainer = new Trainer();
        mockTrainer.setUser(trainerUser);
        mockTrainer.setSpecialization(mockTrainingType);

        User traineeUser = new User();
        traineeUser.setUsername(TRAINEE_USER);
        mockTrainee = new Trainee();
        mockTrainee.setUser(traineeUser);

        mockTrainee.setTrainers(new HashSet<>(Set.of(mockTrainer)));
    }

    @Test
    void createTraining_Success() {
        when(traineeDAO.findByUsernameWithTrainers(TRAINEE_USER)).thenReturn(mockTrainee);
        when(trainerDAO.findByUsername(TRAINER_USER)).thenReturn(mockTrainer);
        when(trainingDAO.save(any(Training.class))).thenReturn(new Training());

        Training result = trainingService.createTraining(
                TRAINEE_USER, TRAINER_USER, TRAINING_NAME, TRAINING_DATE, TRAINING_DURATION);

        assertNotNull(result);
        verify(trainingDAO, times(1)).save(any(Training.class));
        assertEquals(mockTrainingType, result.getTrainingType());
    }

    @Test
    void createTraining_FailsOnMissingFields() {
        assertThrows(ValidationException.class,
                () -> trainingService.createTraining(TRAINEE_USER, TRAINER_USER, TRAINING_NAME, TRAINING_DATE, null));

        assertThrows(ValidationException.class,
                () -> trainingService.createTraining(TRAINEE_USER, TRAINER_USER, TRAINING_NAME, TRAINING_DATE, 0));
    }

    @Test
    void createTraining_FailsOnTraineeNotFound() {
        when(traineeDAO.findByUsernameWithTrainers(TRAINEE_USER)).thenReturn(null);

        assertThrows(ValidationException.class,
                () -> trainingService.createTraining(TRAINEE_USER, TRAINER_USER, TRAINING_NAME, TRAINING_DATE, TRAINING_DURATION));
    }

    @Test
    void createTraining_FailsOnTrainerNotFound() {
        when(traineeDAO.findByUsernameWithTrainers(TRAINEE_USER)).thenReturn(mockTrainee);
        when(trainerDAO.findByUsername(TRAINER_USER)).thenReturn(null);

        assertThrows(ValidationException.class,
                () -> trainingService.createTraining(TRAINEE_USER, TRAINER_USER, TRAINING_NAME, TRAINING_DATE, TRAINING_DURATION));
    }

    @Test
    void createTraining_FailsOnTrainerNotAssociated() {
        mockTrainee.setTrainers(new HashSet<>());
        when(traineeDAO.findByUsernameWithTrainers(TRAINEE_USER)).thenReturn(mockTrainee);
        when(trainerDAO.findByUsername(TRAINER_USER)).thenReturn(mockTrainer);

        assertThrows(ValidationException.class,
                () -> trainingService.createTraining(TRAINEE_USER, TRAINER_USER, TRAINING_NAME, TRAINING_DATE, TRAINING_DURATION));
    }
}