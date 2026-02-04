package com.company.gym.util;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

public class PasswordUtilTest {

    private final String PLAIN = "myPassword";
    private final String HASH = "$2a$10$abcdefghijklmnopqrstuuuvwxyzABCDEFGHIJKLMNOP";

    @Test
    void hashPassword_Success() {
        String hashedPassword = PasswordUtil.hashPassword(PLAIN);
        assertTrue(BCrypt.checkpw(PLAIN, hashedPassword));
    }

    @Test
    void hashPassword_NullInput() {
        assertNull(PasswordUtil.hashPassword(null));
    }

    @Test
    void checkPassword_Match_Success() {
        assertTrue(PasswordUtil.checkPassword(PLAIN, BCrypt.hashpw(PLAIN, BCrypt.gensalt())));
    }

    @Test
    void checkPassword_NoMatch_Failure() {
        String wrongHash = BCrypt.hashpw("wrongPassword", BCrypt.gensalt());
        assertFalse(PasswordUtil.checkPassword(PLAIN, wrongHash));
    }

    @Test
    void checkPassword_NullInputs_Failure() {
        assertFalse(PasswordUtil.checkPassword(null, HASH));
        assertFalse(PasswordUtil.checkPassword(PLAIN, null));
        assertFalse(PasswordUtil.checkPassword(null, null));
    }

    @Test
    void checkPassword_InvalidHashFormat_Failure() {
        try (MockedStatic<BCrypt> mockedBCrypt = Mockito.mockStatic(BCrypt.class)) {
            mockedBCrypt.when(() -> BCrypt.checkpw(anyString(), anyString())).thenThrow(new IllegalArgumentException("Invalid hash format"));

            assertFalse(PasswordUtil.checkPassword(PLAIN, "invalidFormat"));
        }
    }
}