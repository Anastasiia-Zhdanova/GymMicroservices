package com.company.gym.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationExceptionTest {

    private final String MESSAGE = "Access denied";
    private final Throwable CAUSE = new RuntimeException("Root cause");

    @Test
    void testConstructorWithMessage() {
        AuthenticationException exception = new AuthenticationException(MESSAGE);
        assertEquals(MESSAGE, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        AuthenticationException exception = new AuthenticationException(MESSAGE, CAUSE);
        assertEquals(MESSAGE, exception.getMessage());
        assertEquals(CAUSE, exception.getCause());
    }
}