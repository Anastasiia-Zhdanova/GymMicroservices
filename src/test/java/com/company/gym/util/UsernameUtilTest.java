package com.company.gym.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsernameUtilTest {

    @Test
    void generateBaseUsername_Success_FullNames() {
        String username = UsernameUtil.generateBaseUsername("John", "Doe");
        assertEquals("john.doe", username);
    }

    @Test
    void generateBaseUsername_Success_MixedCaseAndSpaces() {
        String username = UsernameUtil.generateBaseUsername(" Mary ", "Ann Smith ");
        assertEquals("mary.annsmith", username);
    }

    @Test
    void generateBaseUsername_Success_OnlyFirstName() {
        String username = UsernameUtil.generateBaseUsername("Harry", null);
        assertEquals("harry", username);
    }

    @Test
    void generateBaseUsername_Success_OnlyLastName() {
        String username = UsernameUtil.generateBaseUsername(null, "Potter");
        assertEquals("potter", username);
    }

    @Test
    void generateBaseUsername_Fails_OnBothNamesEmptyOrNull() {
        assertThrows(IllegalArgumentException.class,
                () -> UsernameUtil.generateBaseUsername(null, null),
                "Should throw IllegalArgumentException if both names are null.");

        assertThrows(IllegalArgumentException.class,
                () -> UsernameUtil.generateBaseUsername(" ", ""),
                "Should throw IllegalArgumentException if both names are empty/whitespace.");
    }
}