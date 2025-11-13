package com.company.gym.service;

import com.company.gym.dao.TraineeDAO;
import com.company.gym.dao.TrainerDAO;
import com.company.gym.dao.UserDAO;
import com.company.gym.dto.response.AuthResponse;
import com.company.gym.entity.Trainee;
import com.company.gym.entity.Trainer;
import com.company.gym.entity.Training;
import com.company.gym.entity.User;
import com.company.gym.exception.NotFoundException;
import com.company.gym.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;
    private final UserDAO userDAO;
    private final AuthService authService;

    public TraineeService(TraineeDAO traineeDAO,
                          TrainerDAO trainerDAO,
                          UserDAO userDAO,
                          AuthService authService) {
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
        this.userDAO = userDAO;
        this.authService = authService;
    }

    @Transactional
    public AuthResponse createProfile(String firstName, String lastName, Date dateOfBirth, String address) {
        logger.info("Attempting to create Trainee profile for {} {}", firstName, lastName);

        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("First name and last name are required.");
        }

        User user = new User();
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());

        var plainPassword = authService.assignUniqueUsernameAndPassword(user);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address == null ? null : address.trim());

        Trainee savedTrainee = traineeDAO.save(trainee);
        logger.info("Trainee profile created. Username: {}", savedTrainee.getUser().getUsername());
        return new AuthResponse(savedTrainee.getUser().getUsername(), plainPassword);
    }

    @Transactional(readOnly = true)
    public Trainee selectProfile(String username) {
        logger.info("Selecting Trainee profile with trainers by username: {}", username);
        Trainee trainee = traineeDAO.findByUsernameWithTrainers(username);

        if (trainee == null) {
            logger.warn("Trainee profile not found for username: {}", username);
            throw new NotFoundException("Trainee profile not found: " + username);
        }

        logger.info("Trainee profile selected successfully: {}", username);
        return trainee;
    }

    private Trainee selectLightweightProfile(String username) {
        logger.info("Selecting Trainee profile by username: {}", username);
        Trainee trainee = traineeDAO.findByUsername(username);

        if (trainee == null) {
            logger.warn("Trainee profile not found for username: {}", username);
            throw new NotFoundException("Trainee profile not found: " + username);
        }

        logger.info("Trainee profile selected successfully: {}", username);
        return trainee;
    }

    @Transactional
    public Trainee updateProfile(String username, String firstName, String lastName, Date dateOfBirth, String address) {
        logger.info("Attempting to update Trainee profile for {}", username);

        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("First name and last name are required.");
        }

        Trainee trainee = selectLightweightProfile(username);

        User user = trainee.getUser();
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());

        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address == null ? null : address.trim());

        userDAO.update(user);
        traineeDAO.update(trainee);
        logger.info("Trainee profile {} updated successfully.", username);
        return traineeDAO.findByUsernameWithTrainers(username);
    }

    @Transactional
    public void deleteProfile(String username) {
        logger.info("Attempting to delete Trainee profile for {}", username);

        Trainee trainee = selectLightweightProfile(username);

        traineeDAO.delete(trainee);
        logger.info("Trainee profile {} deleted successfully (cascade delete occurred).", username);
    }

    @Transactional
    public void activateDeactivateProfile(String username, Boolean isActive) {
        logger.info("Changing Trainee {} active status to {}", username, isActive);

        Trainee trainee = selectLightweightProfile(username);

        User user = trainee.getUser();
        user.setIsActive(isActive);
        trainee.setUser(user);

        userDAO.update(user);
        logger.info("Trainee {} status changed to {}.", username, user.getIsActive());
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainingsList(String username, Date fromDate, Date toDate, String trainerName, String trainingTypeName) {
        logger.info("Fetching trainings list for Trainee {} with filters.", username);

        return traineeDAO.getTraineeTrainingsList(username, fromDate, toDate, trainerName, trainingTypeName);
    }

    @Transactional
    public Trainee updateTrainersList(String traineeUsername, Set<String> trainerUsernames) {
        logger.info("Updating trainers list for Trainee {}.", traineeUsername);

        Trainee trainee = selectProfile(traineeUsername);

        Set<Trainer> newTrainers = trainerUsernames.stream()
                .map(trainerDAO::findByUserNameWithTrainees)
                .filter(trainer -> {
                    if (trainer == null) {
                        logger.warn("Trainer with username {} not found. Skipping.", trainee.getUser().getUsername());
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toSet());

        trainee.getTrainers().clear();
        newTrainers.forEach(trainee::addTrainer);

        Trainee updatedTrainee = traineeDAO.update(trainee);
        logger.info("Trainee {} trainers list updated with {} valid trainers.", traineeUsername, updatedTrainee.getTrainers().size());
        return updatedTrainee;
    }
}