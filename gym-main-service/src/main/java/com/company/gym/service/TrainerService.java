package com.company.gym.service;

import com.company.gym.dao.TrainerDAO;
import com.company.gym.dao.TrainingTypeDAO;
import com.company.gym.dao.UserDAO;
import com.company.gym.dto.response.AuthResponse;
import com.company.gym.entity.Trainer;
import com.company.gym.entity.Training;
import com.company.gym.entity.TrainingType;
import com.company.gym.entity.User;
import com.company.gym.exception.NotFoundException;
import com.company.gym.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);
    private final TrainerDAO trainerDAO;
    private final UserDAO userDAO;
    private final TrainingTypeDAO trainingTypeDAO;
    private final AuthService authService;

    public TrainerService(TrainerDAO trainerDAO,
                          UserDAO userDAO,
                          TrainingTypeDAO trainingTypeDAO,
                          AuthService authService) {
        this.trainerDAO = trainerDAO;
        this.userDAO = userDAO;
        this.trainingTypeDAO = trainingTypeDAO;
        this.authService = authService;
    }

    @Transactional
    public AuthResponse createProfile(String firstName, String lastName, Long specializationId) {
        logger.info("Attempting to create Trainer profile for {} {}", firstName, lastName);

        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty() || specializationId == null) {
            throw new ValidationException("First name, last name, and specialization ID are required.");
        }

        TrainingType specialization = trainingTypeDAO.findById(specializationId);

        if (specialization == null) {
            throw new ValidationException("Training Type with ID " + specializationId + " not found. Cannot create profile.");
        }

        User user = new User();
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());

        var plainPassword = authService.assignUniqueUsernameAndPassword(user);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);

        Trainer savedTrainer = trainerDAO.save(trainer);
        logger.info("Trainer profile created successfully. Username: {}", savedTrainer.getUser().getUsername());
        return new AuthResponse(savedTrainer.getUser().getUsername(), plainPassword);
    }

    @Transactional(readOnly = true)
    public Trainer selectProfile(String username) {
        logger.info("Selecting Trainer profile with trainees by username: {}", username);
        Trainer trainer = trainerDAO.findByUserNameWithTrainees(username);

        if (trainer == null) {
            logger.warn("Trainer profile not found for username: {}", username);
            throw new NotFoundException("Trainer profile not found: " + username);
        }

        logger.info("Trainer profile selected successfully: {}", username);
        return trainer;
    }

    @Transactional
    public Trainer updateProfile(String username, String firstName, String lastName, Long specializationId) {
        logger.info("Attempting to update Trainer profile for {}", username);

        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("First name and last name are required.");
        }

        Trainer trainer = selectLightweightProfile(username);

        if (!trainer.getSpecialization().getId().equals(specializationId)) {
            logger.warn("Attempted to change specialization for Trainer {}. Operation denied as specialization is read-only.", username);
        }

        User user = trainer.getUser();
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());

        userDAO.update(user);
        logger.info("Trainer profile {} updated successfully.", username);
        return trainerDAO.findByUserNameWithTrainees(username);
    }

    @Transactional
    public void activateDeactivateProfile(String username, Boolean isActive) {
        logger.info("Changing Trainer {} active status to {}", username, isActive);

        Trainer trainer = selectLightweightProfile(username);

        User user = trainer.getUser();
        user.setIsActive(isActive);
        trainer.setUser(user);

        userDAO.update(user);
        logger.info("Trainer {} status changed to {}.", username, user.getIsActive());
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainingsList(String username, Date fromDate, Date toDate) {
        logger.info("Fetching trainings list for Trainer {} with date filters.", username);

        return trainerDAO.getTrainerTrainingsList(username, fromDate, toDate);
    }

    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainers(String traineeUsername) {
        logger.info("Fetching trainers not assigned to Trainee: {}", traineeUsername);

        return trainerDAO.findUnassignedTrainers(traineeUsername);
    }

    private Trainer selectLightweightProfile(String username) {
        logger.info("Selecting Trainer profile by username: {}", username);
        Trainer trainer = trainerDAO.findByUsername(username);

        if (trainer == null) {
            logger.warn("Trainer profile not found for username: {}", username);
            throw new NotFoundException("Trainer profile not found: " + username);
        }

        logger.info("Trainer profile selected successfully: {}", username);
        return trainer;
    }
}