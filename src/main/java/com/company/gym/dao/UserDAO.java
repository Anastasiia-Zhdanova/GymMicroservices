package com.company.gym.dao;

import com.company.gym.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDAO extends GenericDAO<User, Long>{
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    @PersistenceContext
    private EntityManager entityManager;

    public UserDAO() {
        super(User.class);
    }

    @Override
    public Long getEntityId(User user) {
        return user.getId();
    }

    public User findByUsername(String username) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT t FROM User t WHERE t.username = :username", User.class);
            query.setParameter("username", username);

            List<User> results = query.getResultList();
            User user = results.isEmpty() ? null : results.get(0);

            if (user == null) {
                logger.debug("User not found with username: {}", username);
            } else {
                logger.debug("Found User with username: {}", username);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error finding User by username: {}", username, e);
            return null;
        }
    }
}