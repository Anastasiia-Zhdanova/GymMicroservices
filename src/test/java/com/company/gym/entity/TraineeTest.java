package com.company.gym.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeTest {

    private Trainee trainee;
    private User mockUser;
    private Trainer mockTrainer;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("jane.doe");

        mockTrainer = new Trainer();
        mockTrainer.setId(2L);

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(mockUser);
        trainee.setDateOfBirth(new Date());
        trainee.setAddress("123 Gym Street");
        trainee.setTrainers(new HashSet<>());
        trainee.setTrainings(new HashSet<>());
    }

    @Test
    void testSettersAndGetters() {
        assertEquals(1L, trainee.getId());
        assertEquals(mockUser, trainee.getUser());
        assertEquals("123 Gym Street", trainee.getAddress());
        assertNotNull(trainee.getDateOfBirth());
        assertNotNull(trainee.getTrainers());
        assertNotNull(trainee.getTrainings());

        Trainee newTrainee = new Trainee();
        assertNotNull(newTrainee.getTrainers());
    }

    @Test
    void testAddTrainer_Success() {
        Trainer newTrainer = new Trainer();
        newTrainer.setId(3L);
        newTrainer.setTrainees(null);

        assertEquals(0, trainee.getTrainers().size());

        trainee.addTrainer(newTrainer);

        assertTrue(trainee.getTrainers().contains(newTrainer));
        assertTrue(newTrainer.getTrainees().contains(trainee));
        assertEquals(1, trainee.getTrainers().size());
        assertNotNull(newTrainer.getTrainees());
    }

    @Test
    void testRemoveTrainer_Success() {
        trainee.addTrainer(mockTrainer);
        assertTrue(trainee.getTrainers().contains(mockTrainer));

        trainee.removeTrainer(mockTrainer);

        assertFalse(trainee.getTrainers().contains(mockTrainer));
        assertFalse(mockTrainer.getTrainees().contains(trainee));
    }

    @Test
    void testRemoveTrainer_FromNullSet() {
        Trainee traineeWithNullSet = new Trainee();
        traineeWithNullSet.setId(1L);
        traineeWithNullSet.setTrainers(null);

        traineeWithNullSet.removeTrainer(mockTrainer);
        assertNull(traineeWithNullSet.getTrainers());
    }

    @Test
    void testEqualsAndHashCode_SameId() {
        Trainee t1 = new Trainee();
        t1.setId(10L);
        Trainee t2 = new Trainee();
        t2.setId(10L);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}