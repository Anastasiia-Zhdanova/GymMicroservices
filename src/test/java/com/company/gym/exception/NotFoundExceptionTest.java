package com.company.gym.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NotFoundExceptionTest {

    private final String MESSAGE = "Resource not found";
    private final Throwable CAUSE = new RuntimeException("Root cause");

    @Test
    void testConstructorWithMessage() {
        NotFoundException exception = new NotFoundException(MESSAGE);
        assertEquals(MESSAGE, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        NotFoundException exception = new NotFoundException(MESSAGE, CAUSE);
        assertEquals(MESSAGE, exception.getMessage());
        assertEquals(CAUSE, exception.getCause());
    }
}