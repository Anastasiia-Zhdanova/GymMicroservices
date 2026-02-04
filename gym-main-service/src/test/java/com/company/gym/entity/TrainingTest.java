package com.company.gym.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class TrainingTest {

    private Training training;
    private Trainee mockTrainee;
    private Trainer mockTrainer;
    private TrainingType mockType;
    private Date mockDate;

    @BeforeEach
    void setUp() {
        mockDate = new Date();
        mockTrainee = new Trainee();
        mockTrainer = new Trainer();
        mockType = new TrainingType("Cardio");

        training = new Training();
        training.setId(1L);
        training.setTrainingName("Morning Run");
        training.setTrainingDate(mockDate);
        training.setTrainingDuration(60);
        training.setTrainee(mockTrainee);
        training.setTrainer(mockTrainer);
        training.setTrainingType(mockType);
    }

    @Test
    void testDefaultConstructorAndGetters() {
        Training newTraining = new Training();
        assertNull(newTraining.getId());
    }

    @Test
    void testSettersAndGetters() {
        assertEquals(1L, training.getId());
        assertEquals("Morning Run", training.getTrainingName());
        assertEquals(mockDate, training.getTrainingDate());
        assertEquals(60, training.getTrainingDuration());
        assertEquals(mockTrainee, training.getTrainee());
        assertEquals(mockTrainer, training.getTrainer());
        assertEquals(mockType, training.getTrainingType());

        training.setTrainingDuration(90);
        assertEquals(90, training.getTrainingDuration());
    }

    @Test
    void testEqualsAndHashCode_SameId() {
        Training t1 = new Training();
        t1.setId(10L);
        Training t2 = new Training();
        t2.setId(10L);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentId() {
        Training t1 = new Training();
        t1.setId(10L);
        Training t2 = new Training();
        t2.setId(11L);

        assertNotEquals(t1, t2);
    }
}