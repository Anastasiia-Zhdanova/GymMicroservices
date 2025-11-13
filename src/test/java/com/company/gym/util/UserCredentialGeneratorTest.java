package com.company.gym.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

public class UserCredentialGeneratorTest {

    @Test
    public void generatePassword_ReturnsCorrectLength() {
        String password = UserCredentialGenerator.generatePassword();
        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    public void generatePassword_ReturnsDifferentPasswords() {
        String password1 = UserCredentialGenerator.generatePassword();
        String password2 = UserCredentialGenerator.generatePassword();
        assertNotEquals(password1, password2);
    }

    @Test
    public void generatePassword_ContainsOnlyValidCharacters() {
        String password = UserCredentialGenerator.generatePassword();
        assertTrue(password.matches("[a-f0-9\\-]+"));
    }

    @Test
    public void constructor_IsPrivate() {
        Constructor<?>[] constructors = UserCredentialGenerator.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructors[0].getModifiers()));

        try {
            constructors[0].setAccessible(true);
            constructors[0].newInstance();
        } catch (Exception e) {
            fail("Failed to call private constructor reflectively: " + e.getMessage());
        }
    }
}