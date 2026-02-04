package com.company.gym.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserCredentialsResponseTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "temp_password";

    @Test
    void testConstructorAndGetters() {
        UserCredentialsResponse response = new UserCredentialsResponse(TEST_USERNAME, TEST_PASSWORD);

        assertNotNull(response, "Object should be successfully created.");

        assertEquals(TEST_USERNAME, response.getUsername(), "Username should match the constructor argument.");
        assertEquals(TEST_PASSWORD, response.getPassword(), "Password should match the constructor argument.");
    }
}