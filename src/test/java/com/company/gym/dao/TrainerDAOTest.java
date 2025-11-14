package com.company.gym.dao;

import com.company.gym.entity.Trainer;
import com.company.gym.entity.Training;
import com.company.gym.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerDAOTest {

    @InjectMocks
    private TrainerDAO trainerDAO;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Trainer> trainerQuery;

    @Mock
    private TypedQuery<Training> trainingQuery;

    @Mock
    private TypedQuery<Long> longQuery;

    private Trainer testTrainer;
    private User testUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(trainerDAO, "entityManager", entityManager);
        ReflectionTestUtils.setField(trainerDAO, GenericDAO.class, "entityManager", entityManager, EntityManager.class);

        testUser = new User();
        testUser.setUsername("trainer.user");

        testTrainer = new Trainer();
        testTrainer.setId(1L);
        testTrainer.setUser(testUser);
    }

    @Test
    void testFindByUsername_Found() {
        when(entityManager.createQuery(contains("WHERE u.username = :username"), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("username", "trainer.user")).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(List.of(testTrainer));

        Trainer found = trainerDAO.findByUsername("trainer.user");

        assertNotNull(found);
        assertEquals("trainer.user", found.getUser().getUsername());
    }

    @Test
    void testFindByUserNameWithTrainees_Found() {
        when(entityManager.createQuery(contains("LEFT JOIN FETCH t.trainees"), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("username", "trainer.user")).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(List.of(testTrainer));

        Trainer found = trainerDAO.findByUserNameWithTrainees("trainer.user");

        assertNotNull(found);
        assertEquals("trainer.user", found.getUser().getUsername());
    }

    @Test
    void testGetTrainerTrainingsList_WithDates() {
        String username = "trainer.user";
        Date fromDate = new Date(1000);
        Date toDate = new Date(2000);

        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(trainingQuery);
        when(trainingQuery.setParameter(anyString(), any())).thenReturn(trainingQuery);
        when(trainingQuery.getResultList()).thenReturn(List.of(new Training()));

        trainerDAO.getTrainerTrainingsList(username, fromDate, toDate);

        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(hqlCaptor.capture(), eq(Training.class));

        String hql = hqlCaptor.getValue();
        assertTrue(hql.contains("t.trainingDate >= :fromDate"));
        assertTrue(hql.contains("t.trainingDate <= :toDate"));

        verify(trainingQuery).setParameter("username", username);
        verify(trainingQuery).setParameter("fromDate", fromDate);
        verify(trainingQuery).setParameter("toDate", toDate);
    }

    @Test
    void testFindUnassignedTrainers_Success() {
        String traineeUsername = "trainee.user";
        Long traineeId = 10L;

        when(entityManager.createQuery(contains("SELECT t.id FROM Trainee t"), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("username", traineeUsername)).thenReturn(longQuery);
        when(longQuery.getResultList()).thenReturn(List.of(traineeId));

        when(entityManager.createQuery(contains("WHERE t.id NOT IN"), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("traineeId", traineeId)).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(List.of(testTrainer));

        List<Trainer> unassigned = trainerDAO.findUnassignedTrainers(traineeUsername);

        assertFalse(unassigned.isEmpty());
        assertEquals(1, unassigned.size());
        assertEquals(testTrainer, unassigned.get(0));

        verify(longQuery).setParameter("username", traineeUsername);
        verify(trainerQuery).setParameter("traineeId", traineeId);
    }

    @Test
    void testFindUnassignedTrainers_TraineeNotFound() {
        String traineeUsername = "unknown.trainee";

        when(entityManager.createQuery(contains("SELECT t.id FROM Trainee t"), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("username", traineeUsername)).thenReturn(longQuery);
        when(longQuery.getResultList()).thenReturn(List.of());

        List<Trainer> unassigned = trainerDAO.findUnassignedTrainers(traineeUsername);

        assertNotNull(unassigned);
        assertTrue(unassigned.isEmpty());

        verify(entityManager, never()).createQuery(contains("WHERE t.id NOT IN"), eq(Trainer.class));
    }

    @Test
    void testFindUnassignedTrainers_Exception() {
        String traineeUsername = "trainee.user";
        when(entityManager.createQuery(contains("SELECT t.id FROM Trainee t"), eq(Long.class)))
                .thenThrow(new RuntimeException("DB error"));

        List<Trainer> unassigned = trainerDAO.findUnassignedTrainers(traineeUsername);

        assertNotNull(unassigned);
        assertTrue(unassigned.isEmpty());
    }
}