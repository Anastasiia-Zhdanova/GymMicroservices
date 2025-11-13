package com.company.gym.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    private record TestErrorResponse(String timestamp, int status, String error, String message) {}

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    private TestErrorResponse getBody(ResponseEntity responseEntity) {
        Object body = responseEntity.getBody();
        try {
            Class<?> errorResponseClass = Class.forName("com.company.gym.exception.GlobalExceptionHandler$ErrorResponse");
            if (errorResponseClass.isInstance(body)) {
                Method timestampMethod = errorResponseClass.getMethod("timestamp");
                Method statusMethod = errorResponseClass.getMethod("status");
                Method errorMethod = errorResponseClass.getMethod("error");
                Method messageMethod = errorResponseClass.getMethod("message");

                return new TestErrorResponse(
                        (String) timestampMethod.invoke(body),
                        (Integer) statusMethod.invoke(body),
                        (String) errorMethod.invoke(body),
                        (String) messageMethod.invoke(body)
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract body fields using Reflection: " + e.getMessage(), e);
        }

        throw new IllegalArgumentException("Response body is not a valid ErrorResponse instance.");
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequest_WithFieldErrors() {
        String fieldName = "username";
        String errorMessage = "Username is required.";

        FieldError fieldError = new FieldError("objectName", fieldName, errorMessage);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity responseEntity = handler.handleValidationExceptions(ex);
        TestErrorResponse responseBody = getBody(responseEntity);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.status());
        assertEquals("Bad Request", responseBody.error());

        assertTrue(responseBody.message().contains(fieldName));
        assertTrue(responseBody.message().contains(errorMessage));
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        String testMessage = "Custom business logic failure.";
        ValidationException ex = new ValidationException(testMessage);

        ResponseEntity responseEntity = handler.handleValidationException(ex);
        TestErrorResponse responseBody = getBody(responseEntity);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.status());
        assertEquals("Bad Request", responseBody.error());
        assertEquals(testMessage, responseBody.message());
    }

    @Test
    void handleAuthenticationException_ShouldReturnUnauthorized() {
        String testMessage = "Access denied.";
        AuthenticationException ex = new AuthenticationException(testMessage);

        ResponseEntity responseEntity = handler.handleAuthenticationException(ex);
        TestErrorResponse responseBody = getBody(responseEntity);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());

        assertEquals(HttpStatus.UNAUTHORIZED.value(), responseBody.status());
        assertEquals("Unauthorized", responseBody.error());
        assertEquals(testMessage, responseBody.message());
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFound() {
        String testMessage = "Trainee profile not found.";
        NotFoundException ex = new NotFoundException(testMessage);

        ResponseEntity responseEntity = handler.handleNotFoundException(ex);
        TestErrorResponse responseBody = getBody(responseEntity);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        assertEquals(HttpStatus.NOT_FOUND.value(), responseBody.status());
        assertEquals("Not Found", responseBody.error());
        assertEquals(testMessage, responseBody.message());
    }

    @Test
    void handleGeneralException_ShouldReturnInternalServerError() {
        Exception ex = new RuntimeException("Database connection timeout.");

        ResponseEntity responseEntity = handler.handleGeneralException(ex);
        TestErrorResponse responseBody = getBody(responseEntity);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseBody.status());
        assertEquals("Internal Server Error", responseBody.error());
        assertTrue(responseBody.message().contains("An unexpected error occurred. Please try again later."));
    }
}