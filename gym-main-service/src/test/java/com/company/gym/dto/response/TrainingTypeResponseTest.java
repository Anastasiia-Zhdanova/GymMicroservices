package com.company.gym.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TrainingTypeResponseTest {

    private static final Long TEST_ID = 1L;
    private static final String TEST_NAME = "Yoga";
    private static final String NEW_NAME = "Pilates";
    private static final Long NEW_ID = 2L;

    @Test
    void testDefaultConstructorAndSetters() {
        TrainingTypeResponse response = new TrainingTypeResponse();

        assertNotNull(response, "Default constructor should create a non-null object.");

        response.setId(TEST_ID);
        response.setTrainingTypeName(TEST_NAME);

        assertEquals(TEST_ID, response.getId(), "ID should be set correctly via setter.");
        assertEquals(TEST_NAME, response.getTrainingTypeName(), "Training type name should be set correctly via setter.");
    }

    @Test
    void testParameterizedConstructorAndGetters() {
        TrainingTypeResponse response = new TrainingTypeResponse(TEST_NAME, TEST_ID);

        assertNotNull(response, "Parameterized constructor should create a non-null object.");

        assertEquals(TEST_ID, response.getId(), "ID should match the constructor argument.");
        assertEquals(TEST_NAME, response.getTrainingTypeName(), "Training type name should match the constructor argument.");
    }
}