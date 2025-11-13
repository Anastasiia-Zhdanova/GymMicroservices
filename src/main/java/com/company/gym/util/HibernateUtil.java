package com.company.gym.util;

import com.company.gym.entity.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

    private static SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");

            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Trainee.class);
            configuration.addAnnotatedClass(Trainer.class);
            configuration.addAnnotatedClass(TrainingType.class);
            configuration.addAnnotatedClass(Training.class);

            sessionFactory = configuration.buildSessionFactory();
            logger.info("Hibernate SessionFactory initialized successfully.");

        } catch (Exception e) {
            logger.error("Initial SessionFactory creation failed.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            logger.info("Hibernate SessionFactory shutdown complete.");
        }
    }
}