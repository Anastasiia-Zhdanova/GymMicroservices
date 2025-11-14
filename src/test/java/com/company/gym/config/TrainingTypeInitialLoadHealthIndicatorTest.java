package com.company.gym.config;

import com.company.gym.dao.TrainingTypeDAO;
import com.company.gym.entity.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingTypeInitialLoadHealthIndicatorTest {

    @Mock
    private TrainingTypeDAO trainingTypeDAO;

    @InjectMocks
    private TrainingTypeInitialLoadHealthIndicator indicator;

    @Test
    void health_ReturnsUp_WhenDataIsFound() {
        List<TrainingType> mockList = List.of(new TrainingType("Yoga"), new TrainingType("Cardio"));
        when(trainingTypeDAO.findAll()).thenReturn(mockList);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(2, health.getDetails().get("count"));
    }

    @Test
    void health_ReturnsDown_WhenDataIsMissing() {
        when(trainingTypeDAO.findAll()).thenReturn(Collections.emptyList());

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("No Training Types found. Initial data load failed.", health.getDetails().get("reason"));
    }
}