package com.company.gym.dao;

import com.company.gym.entity.Trainer;
import com.company.gym.entity.Training;
import com.company.gym.util.HibernateUtil;
import com.company.gym.util.QueryUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerDAOTest {

    private TrainerDAO trainerDAO;
    private final String TEST_USERNAME = "trainerUser";
    private final String TRAINEE_USERNAME = "traineeUser";
    private final Long TRAINEE_ID = 5L;

    @Mock
    private Session session;
    @Mock
    private Query<Trainer> trainerQuery;
    @Mock
    private Query<Training> trainingQuery;
    @Mock
    private Query<Long> longQuery;
    @Mock
    private Trainer mockTrainer;
    @Mock
    private Training mockTraining;

    @BeforeEach
    void setUp() {
        trainerDAO = new TrainerDAO();
        lenient().when(mockTrainer.getId()).thenReturn(1L);

        try (MockedStatic<HibernateUtil> mockedUtil = mockStatic(HibernateUtil.class)) {
            mockedUtil.when(HibernateUtil::getSessionFactory).thenReturn(mock(org.hibernate.SessionFactory.class));
            lenient().when(HibernateUtil.getSessionFactory().openSession()).thenReturn(session);
        }
    }

    private MockedStatic<HibernateUtil> mockHibernateUtil() {
        MockedStatic<HibernateUtil> mockedHibernateUtil = mockStatic(HibernateUtil.class);
        mockedHibernateUtil.when(HibernateUtil::getSessionFactory).thenReturn(mock(org.hibernate.SessionFactory.class));
        when(HibernateUtil.getSessionFactory().openSession()).thenReturn(session);
        return mockedHibernateUtil;
    }

    @Test
    void findByUsername_TrainerFound() {
        when(session.createQuery(anyString(), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("username", TEST_USERNAME)).thenReturn(trainerQuery);
        when(trainerQuery.uniqueResult()).thenReturn(mockTrainer);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            Trainer result = trainerDAO.findByUsername(TEST_USERNAME);

            assertEquals(mockTrainer, result);
            verify(session).close();
        }
    }

    @Test
    void findByUsername_TrainerNotFound() {
        when(session.createQuery(anyString(), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("username", TEST_USERNAME)).thenReturn(trainerQuery);
        when(trainerQuery.uniqueResult()).thenReturn(null);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            Trainer result = trainerDAO.findByUsername(TEST_USERNAME);

            assertNull(result);
            verify(session).close();
        }
    }

    @Test
    void findByUserNameWithTrainees_TrainerFound() {
        when(session.createQuery(anyString(), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("username", TEST_USERNAME)).thenReturn(trainerQuery);
        when(trainerQuery.uniqueResult()).thenReturn(mockTrainer);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            Trainer result = trainerDAO.findByUserNameWithTrainees(TEST_USERNAME);

            assertEquals(mockTrainer, result);
            verify(session).close();
        }
    }

    @Test
    void findByUserNameWithTrainees_TrainerNotFound() {
        when(session.createQuery(anyString(), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("username", TEST_USERNAME)).thenReturn(trainerQuery);
        when(trainerQuery.uniqueResult()).thenReturn(null);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            Trainer result = trainerDAO.findByUserNameWithTrainees(TEST_USERNAME);

            assertNull(result);
            verify(session).close();
        }
    }

    @Test
    void getTrainerTrainingsList_NoDateFilters() {
        List<Training> expectedList = Collections.singletonList(mockTraining);
        when(trainingQuery.getResultList()).thenReturn(expectedList);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil();
             MockedStatic<QueryUtil> mockedQueryUtil = mockStatic(QueryUtil.class)) {

            mockedQueryUtil.when(() -> QueryUtil.getTrainingQuery(
                            eq(TEST_USERNAME), isNull(), isNull(), eq(session), any(StringBuilder.class)))
                    .thenReturn(trainingQuery);

            List<Training> result = trainerDAO.getTrainerTrainingsList(TEST_USERNAME, null, null);

            assertEquals(expectedList, result);
            verify(session).close();
        }
    }

    @Test
    void getTrainerTrainingsList_WithDateFilters() {
        Date fromDate = new Date();
        Date toDate = new Date();
        List<Training> expectedList = Collections.singletonList(mockTraining);
        when(trainingQuery.getResultList()).thenReturn(expectedList);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil();
             MockedStatic<QueryUtil> mockedQueryUtil = mockStatic(QueryUtil.class)) {

            mockedQueryUtil.when(() -> QueryUtil.getTrainingQuery(
                            eq(TEST_USERNAME), eq(fromDate), eq(toDate), eq(session), any(StringBuilder.class)))
                    .thenReturn(trainingQuery);

            List<Training> result = trainerDAO.getTrainerTrainingsList(TEST_USERNAME, fromDate, toDate);

            assertEquals(expectedList, result);
            verify(session).close();
        }
    }

    @Test
    void findUnassignedTrainers_TraineeNotFound() {
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("username", TRAINEE_USERNAME)).thenReturn(longQuery);
        when(longQuery.uniqueResult()).thenReturn(null);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            List<Trainer> result = trainerDAO.findUnassignedTrainers(TRAINEE_USERNAME);

            assertTrue(result.isEmpty());
            verify(session).close();
        }
    }

    @Test
    void findUnassignedTrainers_Success() {
        List<Trainer> expectedList = Collections.singletonList(mockTrainer);
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("username", TRAINEE_USERNAME)).thenReturn(longQuery);
        when(longQuery.uniqueResult()).thenReturn(TRAINEE_ID);

        when(session.createQuery(argThat(s -> s.contains("NOT IN")), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("traineeId", TRAINEE_ID)).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(expectedList);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            List<Trainer> result = trainerDAO.findUnassignedTrainers(TRAINEE_USERNAME);

            assertEquals(expectedList, result);
            verify(session).close();
        }
    }

    @Test
    void findUnassignedTrainers_ExceptionHandling() {
        when(session.createQuery(anyString(), eq(Long.class))).thenThrow(new RuntimeException("DB error"));

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            List<Trainer> result = trainerDAO.findUnassignedTrainers(TRAINEE_USERNAME);

            assertTrue(result.isEmpty());
            verify(session).close();
        }
    }
}