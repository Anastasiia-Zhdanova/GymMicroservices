package com.company.gym.dao;

import com.company.gym.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(UserDAO.class)
public class GenericDAOTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserDAO userDAO;

    private User testUser;
    private Long testId;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFirstName("Generic");
        testUser.setLastName("Test");
        testUser.setUsername("generic.test");
        testUser.setPassword("pass");
        testUser.setIsActive(true);
    }

    @Test
    @Transactional
    void save_Success() {
        User savedUser = userDAO.save(testUser);

        assertNotNull(savedUser.getId());
        assertEquals(testUser.getUsername(), savedUser.getUsername());

        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertEquals(savedUser.getUsername(), foundUser.getUsername());
    }

    @Test
    @Transactional
    void update_Success() {
        User savedUser = entityManager.persistFlushFind(testUser);
        savedUser.setFirstName("UpdatedName");

        User updatedUser = userDAO.update(savedUser);

        assertEquals("UpdatedName", updatedUser.getFirstName());
    }

    @Test
    @Transactional
    void delete_Success() {
        User savedUser = entityManager.persistFlushFind(testUser);
        testId = savedUser.getId();

        assertNotNull(entityManager.find(User.class, testId));

        userDAO.delete(savedUser);

        assertNull(entityManager.find(User.class, testId));
    }

    @Test
    void findById_Found() {
        User savedUser = entityManager.persistFlushFind(testUser);
        testId = savedUser.getId();

        User foundUser = userDAO.findById(testId);

        assertEquals(testUser.getUsername(), foundUser.getUsername());
    }

    @Test
    void findById_NotFound() {
        User foundUser = userDAO.findById(999L);
        assertNull(foundUser);
    }

    @Test
    void findAll_ReturnsList() {
        entityManager.persist(testUser);

        User user2 = new User();
        user2.setFirstName("User");
        user2.setLastName("Two");
        user2.setUsername("user.two");
        user2.setPassword("p");
        user2.setIsActive(true);
        entityManager.persist(user2);

        List<User> users = userDAO.findAll();

        assertEquals(2, users.size());
    }
}