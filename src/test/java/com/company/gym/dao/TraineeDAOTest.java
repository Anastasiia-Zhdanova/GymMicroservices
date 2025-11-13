package com.company.gym.dao;

import com.company.gym.entity.Trainee;
import com.company.gym.entity.Training;
import com.company.gym.util.HibernateUtil;
import com.company.gym.util.QueryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TraineeDAOTest {

    private final String TEST_USERNAME = "traineeUser";
    private final Long TEST_ID = 2L;

    @Spy
    @InjectMocks
    private TraineeDAO traineeDAO;

    @Mock
    private Session session;
    @Mock
    private Trainee mockTrainee;
    @Mock
    private Training mockTraining;
    @Mock
    private Query<Trainee> traineeQuery;
    @Mock
    private Query<Training> trainingQuery;
    @Mock
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        lenient().when(mockTrainee.getId()).thenReturn(TEST_ID);
        try (MockedStatic<HibernateUtil> mockedUtil = mockStatic(HibernateUtil.class)) {
            mockedUtil.when(HibernateUtil::getSessionFactory).thenReturn(mock(org.hibernate.SessionFactory.class));
            lenient().when(HibernateUtil.getSessionFactory().openSession()).thenReturn(session);
            lenient().when(session.beginTransaction()).thenReturn(transaction);
        }
    }

    private MockedStatic<HibernateUtil> mockHibernateUtil() {
        MockedStatic<HibernateUtil> mockedHibernateUtil = mockStatic(HibernateUtil.class);
        mockedHibernateUtil.when(HibernateUtil::getSessionFactory).thenReturn(mock(org.hibernate.SessionFactory.class));
        when(HibernateUtil.getSessionFactory().openSession()).thenReturn(session);
        return mockedHibernateUtil;
    }


    @Test
    void findByUsername_TraineeFound() {
        when(session.createQuery(anyString(), eq(Trainee.class))).thenReturn(traineeQuery);
        when(traineeQuery.setParameter("username", TEST_USERNAME)).thenReturn(traineeQuery);
        when(traineeQuery.uniqueResult()).thenReturn(mockTrainee);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            Trainee result = traineeDAO.findByUsername(TEST_USERNAME);

            assertEquals(mockTrainee, result);
            verify(session).close();
        }
    }

    @Test
    void findByUsername_TraineeNotFound() {
        when(session.createQuery(anyString(), eq(Trainee.class))).thenReturn(traineeQuery);
        when(traineeQuery.setParameter("username", TEST_USERNAME)).thenReturn(traineeQuery);
        when(traineeQuery.uniqueResult()).thenReturn(null);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            Trainee result = traineeDAO.findByUsername(TEST_USERNAME);

            assertNull(result);
            verify(session).close();
        }
    }

    @Test
    void findByUsernameWithTrainers_TraineeFound() {
        when(session.createQuery(anyString(), eq(Trainee.class))).thenReturn(traineeQuery);
        when(traineeQuery.setParameter("username", TEST_USERNAME)).thenReturn(traineeQuery);
        when(traineeQuery.uniqueResult()).thenReturn(mockTrainee);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            Trainee result = traineeDAO.findByUsernameWithTrainers(TEST_USERNAME);

            assertEquals(mockTrainee, result);
            verify(session).close();
        }
    }

    @Test
    void delete_CallsSuperAndDelete() {
        doNothing().when(traineeDAO).delete(mockTrainee);

        traineeDAO.delete(mockTrainee);

        verify(traineeDAO).delete(mockTrainee);
    }

    @Test
    void getTraineeTrainingsList_AllFiltersNull() {
        List<Training> expectedList = Collections.singletonList(mockTraining);
        when(trainingQuery.list()).thenReturn(expectedList);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil();
             MockedStatic<QueryUtil> mockedQueryUtil = mockStatic(QueryUtil.class)) {

            mockedQueryUtil.when(() -> QueryUtil.getTrainingQuery(
                            eq(TEST_USERNAME), isNull(), isNull(), eq(session), any(StringBuilder.class)))
                    .thenReturn(trainingQuery);

            List<Training> result = traineeDAO.getTraineeTrainingsList(TEST_USERNAME, null, null, null, null);

            assertEquals(expectedList, result);
            verify(session).close();
        }
    }

    @Test
    void getTraineeTrainingsList_WithAllFilters() {
        Date fromDate = new Date();
        Date toDate = new Date();
        String trainerName = "John Doe";
        String trainingTypeName = "Yoga";

        List<Training> expectedList = Collections.singletonList(mockTraining);
        when(trainingQuery.list()).thenReturn(expectedList);
        when(trainingQuery.setParameter("trainerName", "%" + trainerName + "%")).thenReturn(trainingQuery);
        when(trainingQuery.setParameter("trainingTypeName", trainingTypeName)).thenReturn(trainingQuery);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil();
             MockedStatic<QueryUtil> mockedQueryUtil = mockStatic(QueryUtil.class)) {

            mockedQueryUtil.when(() -> QueryUtil.getTrainingQuery(
                            eq(TEST_USERNAME), eq(fromDate), eq(toDate), eq(session), any(StringBuilder.class)))
                    .thenReturn(trainingQuery);

            List<Training> result = traineeDAO.getTraineeTrainingsList(TEST_USERNAME, fromDate, toDate, trainerName, trainingTypeName);

            assertEquals(expectedList, result);
            verify(trainingQuery).setParameter("trainerName", "%" + trainerName + "%");
            verify(trainingQuery).setParameter("trainingTypeName", trainingTypeName);
            verify(session).close();
        }
    }
}