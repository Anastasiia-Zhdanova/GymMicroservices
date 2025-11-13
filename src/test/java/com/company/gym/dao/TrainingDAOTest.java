package com.company.gym.dao;

import com.company.gym.entity.Training;
import com.company.gym.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class TrainingDAOTest {

    @Mock
    private Session session;
    @Mock
    private SessionFactory sessionFactory;

    @InjectMocks
    private TrainingDAO trainingDAO;

    private MockedStatic<HibernateUtil> mockedHibernateUtil;
    private Training mockTraining;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockTraining = new Training();
        mockTraining.setId(1L);

        mockedHibernateUtil = Mockito.mockStatic(HibernateUtil.class);
        mockedHibernateUtil.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
    }

    @AfterEach
    void tearDown() {
        mockedHibernateUtil.close();
    }

    @Test
    void getEntityId_Success() {
        Long id = trainingDAO.getEntityId(mockTraining);
        assertEquals(1L, id);
    }
}