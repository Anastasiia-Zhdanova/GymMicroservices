package com.company.gym.dao;

import com.company.gym.entity.Trainee;
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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TraineeDAOTest {

    @InjectMocks
    private TraineeDAO traineeDAO;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Trainee> traineeQuery;

    @Mock
    private TypedQuery<Training> trainingQuery;

    private Trainee testTrainee;
    private User testUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(traineeDAO, "entityManager", entityManager);
        ReflectionTestUtils.setField(traineeDAO, GenericDAO.class, "entityManager", entityManager, EntityManager.class);

        testUser = new User();
        testUser.setUsername("trainee.user");

        testTrainee = new Trainee();
        testTrainee.setId(1L);
        testTrainee.setUser(testUser);
    }

    @Test
    void testFindByUsername_Found() {
        when(entityManager.createQuery(anyString(), eq(Trainee.class))).thenReturn(traineeQuery);
        when(traineeQuery.setParameter("username", "trainee.user")).thenReturn(traineeQuery);
        when(traineeQuery.getResultList()).thenReturn(List.of(testTrainee));

        Trainee found = traineeDAO.findByUsername("trainee.user");

        assertNotNull(found);
        assertEquals("trainee.user", found.getUser().getUsername());
        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(hqlCaptor.capture(), eq(Trainee.class));
        assertTrue(hqlCaptor.getValue().contains("WHERE u.username = :username"));
        assertTrue(hqlCaptor.getValue().contains("Trainee t"));
    }

    @Test
    void testFindByUsernameWithTrainers_Found() {
        when(entityManager.createQuery(anyString(), eq(Trainee.class))).thenReturn(traineeQuery);
        when(traineeQuery.setParameter("username", "trainee.user")).thenReturn(traineeQuery);
        when(traineeQuery.getResultList()).thenReturn(List.of(testTrainee));

        Trainee found = traineeDAO.findByUsernameWithTrainers("trainee.user");

        assertNotNull(found);
        assertEquals("trainee.user", found.getUser().getUsername());
        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(hqlCaptor.capture(), eq(Trainee.class));
        assertTrue(hqlCaptor.getValue().contains("LEFT JOIN FETCH t.trainers"));
    }

    @Test
    void testDelete() {
        when(entityManager.contains(testTrainee)).thenReturn(true);
        doNothing().when(entityManager).remove(testTrainee);

        traineeDAO.delete(testTrainee);

        verify(entityManager).remove(testTrainee);
    }

    @Test
    void testGetTraineeTrainingsList_AllFilters() {
        String username = "trainee.user";
        Date fromDate = new Date(1000);
        Date toDate = new Date(2000);
        String trainerName = "John Doe";
        String trainingTypeName = "Cardio";

        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(trainingQuery);
        when(trainingQuery.setParameter(anyString(), any())).thenReturn(trainingQuery);
        when(trainingQuery.getResultList()).thenReturn(List.of(new Training()));

        List<Training> trainings = traineeDAO.getTraineeTrainingsList(username, fromDate, toDate, trainerName, trainingTypeName);

        assertFalse(trainings.isEmpty());

        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(hqlCaptor.capture(), eq(Training.class));

        String hql = hqlCaptor.getValue();
        assertTrue(hql.contains("t.trainingDate >= :fromDate"));
        assertTrue(hql.contains("t.trainingDate <= :toDate"));
        assertTrue(hql.contains("CONCAT(tn.user.firstName, ' ', tn.user.lastName) LIKE :trainerName"));
        assertTrue(hql.contains("tt.name = :trainingTypeName"));

        verify(trainingQuery).setParameter(eq("username"), eq(username));
        verify(trainingQuery).setParameter(eq("fromDate"), eq(fromDate));
        verify(trainingQuery).setParameter(eq("toDate"), eq(toDate));
        verify(trainingQuery).setParameter(eq("trainerName"), eq("%" + trainerName + "%"));
        verify(trainingQuery).setParameter(eq("trainingTypeName"), eq(trainingTypeName));
    }

    @Test
    void testGetTraineeTrainingsList_NoFilters() {
        String username = "trainee.user";
        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(trainingQuery);
        when(trainingQuery.setParameter(anyString(), any())).thenReturn(trainingQuery);
        when(trainingQuery.getResultList()).thenReturn(Collections.emptyList());

        traineeDAO.getTraineeTrainingsList(username, null, null, null, null);

        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(hqlCaptor.capture(), eq(Training.class));

        String hql = hqlCaptor.getValue();
        assertFalse(hql.contains("t.trainingDate"));
        assertFalse(hql.contains("CONCAT"));
        assertFalse(hql.contains("tt.name"));

        verify(trainingQuery).setParameter(eq("username"), eq(username));
        verify(trainingQuery, never()).setParameter(eq("fromDate"), any());
        verify(trainingQuery, never()).setParameter(eq("trainerName"), anyString());
        verify(trainingQuery, never()).setParameter(eq("trainingTypeName"), anyString());
    }
}