package com.company.gym.service;

import com.company.gym.dao.TraineeDAO;
import com.company.gym.dao.TrainerDAO;
import com.company.gym.dao.TrainingDAO;
import com.company.gym.entity.Trainee;
import com.company.gym.entity.Trainer;
import com.company.gym.entity.Training;
import com.company.gym.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    private final TrainingDAO trainingDAO;
    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;

    public TrainingService(TrainingDAO trainingDAO, TraineeDAO traineeDAO, TrainerDAO trainerDAO) {
        this.trainingDAO = trainingDAO;
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
    }

    @Transactional
    public Training createTraining(String traineeUsername, String trainerUsername, String trainingName, Date trainingDate, Integer trainingDuration) {
        if (traineeUsername == null
                || trainerUsername == null
                || trainingName == null
                || trainingDate == null
                || trainingDuration == null
                || trainingDuration <= 0) {
            throw new ValidationException("All fields (trainee/trainer username, name, date, duration) are required and duration must be positive.");
        }

        Trainee trainee = traineeDAO.findByUsernameWithTrainers(traineeUsername);
        if (trainee == null) {
            throw new ValidationException("Trainee not found with username: " + traineeUsername);
        }

        Trainer trainer = trainerDAO.findByUsername(trainerUsername);
        if (trainer == null) {
            throw new ValidationException("Trainer not found with username: " + trainerUsername);
        }

        if (!trainee.getTrainers().contains(trainer)) {
            throw new ValidationException("Trainer '" + trainerUsername + "' is not associated with Trainee '" + traineeUsername + "'.");
        }

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(trainingName);
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);
        training.setTrainingType(trainer.getSpecialization());

        trainingDAO.save(training);
        logger.info("Training '{}' created for Trainee {} and Trainer {}.", trainingName, traineeUsername, trainerUsername);
        return training;
    }
}