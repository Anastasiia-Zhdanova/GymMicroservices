package com.company.gym.config;

import com.company.gym.dao.TrainingTypeDAO;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("trainingTypesLoaded")
public class TrainingTypeInitialLoadHealthIndicator implements HealthIndicator {

    private final TrainingTypeDAO trainingTypeDAO;

    public TrainingTypeInitialLoadHealthIndicator(TrainingTypeDAO trainingTypeDAO) {
        this.trainingTypeDAO = trainingTypeDAO;
    }

    @Override
    public Health health() {
        if (trainingTypeDAO.findAll().isEmpty()) {
            return Health.down().withDetail("reason", "No Training Types found. Initial data load failed.").build();
        }
        return Health.up().withDetail("count", trainingTypeDAO.findAll().size()).build();
    }
}