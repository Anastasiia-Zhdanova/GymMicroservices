package com.company.gym.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationExceptionTest {

    private final String MESSAGE = "Input data invalid";
    private final Throwable CAUSE = new RuntimeException("Root cause");

    @Test
    void testConstructorWithMessage() {
        ValidationException exception = new ValidationException(MESSAGE);
        assertEquals(MESSAGE, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        ValidationException exception = new ValidationException(MESSAGE, CAUSE);
        assertEquals(MESSAGE, exception.getMessage());
        assertEquals(CAUSE, exception.getCause());
    }
}