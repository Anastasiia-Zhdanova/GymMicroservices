package com.company.gym.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TrainingTypeTest {

    private TrainingType type;

    @BeforeEach
    void setUp() {
        type = new TrainingType("Yoga");
        type.setId(10L);
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals("Yoga", type.getName());
    }

    @Test
    void testSettersAndGetters() {
        assertEquals(10L, type.getId());
        assertEquals("Yoga", type.getName());

        type.setName("Strength");
        type.setId(11L);

        assertEquals(11L, type.getId());
        assertEquals("Strength", type.getName());
    }

    @Test
    void testEqualsAndHashCode_SameId() {
        TrainingType type1 = new TrainingType("Running");
        type1.setId(2L);
        TrainingType type2 = new TrainingType("Pilates");
        type2.setId(2L);

        assertEquals(type1, type2);
        assertEquals(type1.hashCode(), type2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentId() {
        TrainingType type1 = new TrainingType();
        type1.setId(2L);
        TrainingType type2 = new TrainingType();
        type2.setId(3L);

        assertNotEquals(type1, type2);
        assertNotEquals(type1.hashCode(), type2.hashCode());
    }
}