package com.company.gym.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class TrainerTest {

    private Trainer trainer;
    private User mockUser;
    private TrainingType mockType;
    private Trainee mockTrainee;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("mickey.mouse");

        mockType = new TrainingType("Duelling");
        mockType.setId(10L);

        mockTrainee = new Trainee();
        mockTrainee.setId(2L);

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(mockUser);
        trainer.setSpecialization(mockType);
        trainer.setTrainees(new HashSet<>());
        trainer.setTrainings(new HashSet<>());
    }

    @Test
    void testSettersAndGetters() {
        assertEquals(1L, trainer.getId());
        assertEquals(mockUser, trainer.getUser());
        assertEquals(mockType, trainer.getSpecialization());
        assertNotNull(trainer.getTrainees());
        assertNotNull(trainer.getTrainings());
    }

    @Test
    void testAddTrainee_Success() {
        Trainee newTrainee = new Trainee();
        newTrainee.setId(3L);
        newTrainee.setTrainers(null);

        assertEquals(0, trainer.getTrainees().size());

        trainer.addTrainee(newTrainee);

        assertTrue(trainer.getTrainees().contains(newTrainee));
        assertTrue(newTrainee.getTrainers().contains(trainer));
        assertEquals(1, trainer.getTrainees().size());
        assertNotNull(newTrainee.getTrainers());
    }

    @Test
    void testRemoveTrainee_Success() {
        trainer.addTrainee(mockTrainee);
        assertTrue(trainer.getTrainees().contains(mockTrainee));

        trainer.removeTrainee(mockTrainee);

        assertFalse(trainer.getTrainees().contains(mockTrainee));
        assertFalse(mockTrainee.getTrainers().contains(trainer));
    }

    @Test
    void testRemoveTrainee_FromNullSet() {
        Trainer trainerWithNullSet = new Trainer();
        trainerWithNullSet.setId(1L);
        trainerWithNullSet.setTrainees(null);

        trainerWithNullSet.removeTrainee(mockTrainee);
        assertNull(trainerWithNullSet.getTrainees());
    }

    @Test
    void testEqualsAndHashCode_SameId() {
        Trainer t1 = new Trainer();
        t1.setId(10L);
        Trainer t2 = new Trainer();
        t2.setId(10L);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}