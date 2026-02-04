package com.company.gym.dao;

import com.company.gym.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDAOTest {

    @InjectMocks
    private UserDAO userDAO;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<User> typedQuery;

    private User testUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userDAO, "entityManager", entityManager);
        ReflectionTestUtils.setField(userDAO, GenericDAO.class, "entityManager", entityManager, EntityManager.class);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test.user");
    }

    @Test
    void testGetEntityId() {
        assertEquals(1L, userDAO.getEntityId(testUser));
    }

    @Test
    void testFindByUsername_UserFound() {
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", "test.user")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(testUser));

        User foundUser = userDAO.findByUsername("test.user");

        assertNotNull(foundUser);
        assertEquals("test.user", foundUser.getUsername());
        verify(typedQuery).setParameter("username", "test.user");
    }

    @Test
    void testFindByUsername_UserNotFound() {
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", "unknown")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        User foundUser = userDAO.findByUsername("unknown");

        assertNull(foundUser);
    }

    @Test
    void testFindByUsername_Exception() {
        when(entityManager.createQuery(anyString(), eq(User.class))).thenThrow(new RuntimeException("DB error"));

        User foundUser = userDAO.findByUsername("test.user");

        assertNull(foundUser);
    }

    @Test
    void testSave_Success() {
        doNothing().when(entityManager).persist(testUser);

        User savedUser = userDAO.save(testUser);

        assertNotNull(savedUser);
        verify(entityManager).persist(testUser);
    }

    @Test
    void testSave_Exception() {
        doThrow(new RuntimeException("Persist error")).when(entityManager).persist(testUser);

        assertThrows(RuntimeException.class, () -> userDAO.save(testUser));
    }

    @Test
    void testUpdate() {
        when(entityManager.merge(testUser)).thenReturn(testUser);

        User updatedUser = userDAO.update(testUser);

        assertNotNull(updatedUser);
        verify(entityManager).merge(testUser);
    }

    @Test
    void testDelete() {
        when(entityManager.contains(testUser)).thenReturn(true);
        doNothing().when(entityManager).remove(testUser);

        userDAO.delete(testUser);

        verify(entityManager).remove(testUser);
    }

    @Test
    void testDelete_NotInContext() {
        when(entityManager.contains(testUser)).thenReturn(false);
        when(entityManager.merge(testUser)).thenReturn(testUser);
        doNothing().when(entityManager).remove(testUser);

        userDAO.delete(testUser);

        verify(entityManager).merge(testUser);
        verify(entityManager).remove(testUser);
    }

    @Test
    void testFindById_Found() {
        when(entityManager.find(User.class, 1L)).thenReturn(testUser);

        User foundUser = userDAO.findById(1L);

        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
    }

    @Test
    void testFindById_NotFound() {
        when(entityManager.find(User.class, 2L)).thenReturn(null);

        User foundUser = userDAO.findById(2L);

        assertNull(foundUser);
    }

    @Test
    void testFindAll() {
        when(entityManager.createQuery("FROM User", User.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(testUser, new User()));

        List<User> users = userDAO.findAll();

        assertNotNull(users);
        assertEquals(2, users.size());
    }
}