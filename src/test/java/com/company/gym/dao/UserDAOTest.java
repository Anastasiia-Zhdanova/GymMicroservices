package com.company.gym.dao;

import com.company.gym.entity.User;
import com.company.gym.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserDAOTest {

    @Mock
    private Session session;
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Query<User> query;

    @InjectMocks
    private UserDAO userDAO;

    private MockedStatic<HibernateUtil> mockedHibernateUtil;
    private final String USERNAME = "test.user";
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setUsername(USERNAME);

        mockedHibernateUtil = Mockito.mockStatic(HibernateUtil.class);
        mockedHibernateUtil.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
    }

    @AfterEach
    void tearDown() {
        mockedHibernateUtil.close();
    }

    @Test
    void findByUsername_Found() {
        when(session.createQuery(anyString(), Mockito.eq(User.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(mockUser);

        User result = userDAO.findByUsername(USERNAME);

        assertNotNull(result);
        assertEquals(USERNAME, result.getUsername());
        verify(query).setParameter("username", USERNAME);
        verify(session).close();
    }

    @Test
    void findByUsername_NotFound() {
        when(session.createQuery(anyString(), Mockito.eq(User.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        User result = userDAO.findByUsername(USERNAME);

        assertNull(result);
        verify(session).close();
    }
}