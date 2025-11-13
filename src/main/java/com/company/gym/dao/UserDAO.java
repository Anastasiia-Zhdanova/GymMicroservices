package com.company.gym.dao;

import com.company.gym.entity.User;
import com.company.gym.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO extends GenericDAO<User, Long> {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    public UserDAO() {
        super(User.class);
    }

    @Override
    public Long getEntityId(User user) {
        return user.getId();
    }

    public User findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(
                    "SELECT t FROM User t WHERE t.username = :username", User.class);
            query.setParameter("username", username);
            User user = query.uniqueResult();

            if (user == null) {
                logger.debug("User not found with username: {}", username);
            } else {
                logger.debug("Found User with username: {}", username);
            }
            return user;
        }
    }
}