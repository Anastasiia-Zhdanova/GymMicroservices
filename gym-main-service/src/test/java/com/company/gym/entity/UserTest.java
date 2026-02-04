package com.company.gym.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");
        user.setPassword("hashedpassword");
        user.setIsActive(true);
    }

    @Test
    void testDefaultConstructorAndGetters() {
        User newUser = new User();
        assertTrue(newUser.getIsActive());
        assertNull(newUser.getId());
    }

    @Test
    void testSettersAndGetters() {
        assertEquals(1L, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe", user.getUsername());
        assertEquals("hashedpassword", user.getPassword());
        assertTrue(user.getIsActive());

        user.setIsActive(false);
        assertFalse(user.getIsActive());
    }

    @Test
    void testEqualsAndHashCode_SameId() {
        User user1 = new User();
        user1.setId(5L);
        User user2 = new User();
        user2.setId(5L);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentId() {
        User user1 = new User();
        user1.setId(5L);
        User user2 = new User();
        user2.setId(6L);

        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testEquals_NullAndDifferentClass() {
        assertNotEquals(user, null);
        assertNotEquals(user, new Object());
    }
}