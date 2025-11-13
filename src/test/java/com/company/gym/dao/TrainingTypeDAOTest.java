package com.company.gym.dao;

import com.company.gym.entity.TrainingType;
import com.company.gym.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrainingTypeDAOTest {

    @Mock
    private Session session;
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Query<TrainingType> query;

    @InjectMocks
    private TrainingTypeDAO trainingTypeDAO;

    private MockedStatic<HibernateUtil> mockedHibernateUtil;
    private final String TYPE_NAME = "Yoga";
    private TrainingType mockType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockType = new TrainingType(TYPE_NAME);

        mockedHibernateUtil = Mockito.mockStatic(HibernateUtil.class);
        mockedHibernateUtil.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
    }

    @AfterEach
    void tearDown() {
        mockedHibernateUtil.close();
    }

    @Test
    void findByName_Found() {
        when(session.createQuery(anyString(), Mockito.eq(TrainingType.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(mockType);

        TrainingType result = trainingTypeDAO.findByName(TYPE_NAME);

        assertNotNull(result);
        assertEquals(TYPE_NAME, result.getName());
        verify(query).setParameter("name", TYPE_NAME);
        verify(session).close();
    }

    @Test
    void findByName_NotFound() {
        when(session.createQuery(anyString(), Mockito.eq(TrainingType.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        TrainingType result = trainingTypeDAO.findByName(TYPE_NAME);

        assertNull(result);
        verify(session).close();
    }

    @Test
    void findAll_Success() {
        List<TrainingType> expectedList = List.of(mockType);
        when(session.createQuery(anyString(), Mockito.eq(TrainingType.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(expectedList);

        List<TrainingType> result = trainingTypeDAO.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(session).close();
    }
}