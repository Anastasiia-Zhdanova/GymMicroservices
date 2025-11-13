package com.company.gym.service;

import com.company.gym.dao.TrainingTypeDAO;
import com.company.gym.entity.TrainingType;
import com.company.gym.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingTypeService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingTypeService.class);

    private final TrainingTypeDAO trainingTypeDAO;

    public TrainingTypeService(TrainingTypeDAO trainingTypeDAO) {
        this.trainingTypeDAO = trainingTypeDAO;
    }

    @Transactional(readOnly = true)
    public List<TrainingType> getAllTrainingTypes() {
        logger.info("Fetching all available Training Types.");

        List<TrainingType> types = trainingTypeDAO.findAll();

        if (types.isEmpty()) {
            logger.warn("No Training Types found in the database.");
        }

        logger.info("Found {} training types.", types.size());
        return types;
    }

    @Transactional(readOnly = true)
    public TrainingType findTrainingTypeById(Long id) {
        logger.info("Fetching Training Type by ID: {}", id);

        TrainingType type = trainingTypeDAO.findById(id);

        if (type == null) {
            logger.warn("Training Type with ID {} not found.", id);
            throw new NotFoundException("Training Type not found with ID: " + id);
        }

        logger.debug("Successfully fetched Training Type: {}", type.getName());
        return type;
    }
}