package com.company.gym.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testValidLoginRequest_NoViolations() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        assertTrue(validator.validate(request).isEmpty(), "A valid request should have no violations.");
    }

    @Test
    void testGettersAndSetters() {
        LoginRequest request = new LoginRequest();
        request.setUsername("gettertest");
        request.setPassword("settertest");

        assertEquals("gettertest", request.getUsername());
        assertEquals("settertest", request.getPassword());
    }

    @Test
    void testUsernameNotBlankViolation() {
        LoginRequest request = new LoginRequest();
        request.setPassword("valid_password");

        request.setUsername(null);
        assertEquals(1, validator.validate(request).size(), "Username must not be null.");

        request.setUsername("");
        assertEquals(1, validator.validate(request).size(), "Username must not be empty.");

        request.setUsername("  ");
        assertEquals(1, validator.validate(request).size(), "Username must not be whitespace.");
    }

    @Test
    void testPasswordNotBlankViolation() {
        LoginRequest request = new LoginRequest();
        request.setUsername("valid_username");

        request.setPassword(null);
        assertEquals(1, validator.validate(request).size(), "Password must not be null.");

        request.setPassword("");
        assertEquals(1, validator.validate(request).size(), "Password must not be empty.");

        request.setPassword("  ");
        assertEquals(1, validator.validate(request).size(), "Password must not be whitespace.");
    }
}