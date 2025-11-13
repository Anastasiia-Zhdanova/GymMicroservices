package com.company.gym.util;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateUtilTest {

    @Test
    void getSessionFactory_IsNotNull() {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        assertNotNull(factory);
        assertTrue(factory.isOpen());
    }

    @Test
    void getSessionFactory_ReturnsSingleton() {
        SessionFactory factory1 = HibernateUtil.getSessionFactory();
        SessionFactory factory2 = HibernateUtil.getSessionFactory();
        assertSame(factory1, factory2);
    }

    @Test
    void shutdown_ClosesFactory() {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        HibernateUtil.shutdown();
        assertFalse(factory.isOpen());
    }

    @AfterAll
    static void cleanup() {
        HibernateUtil.shutdown();
    }
}