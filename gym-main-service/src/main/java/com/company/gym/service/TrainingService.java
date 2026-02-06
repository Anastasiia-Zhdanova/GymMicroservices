package com.company.gym.service;

import com.company.gym.client.WorkloadClient;
import com.company.gym.dao.TraineeDAO;
import com.company.gym.dao.TrainerDAO;
import com.company.gym.dao.TrainingDAO;
import com.company.gym.dto.request.TrainerWorkloadRequest;
import com.company.gym.entity.Trainee;
import com.company.gym.entity.Trainer;
import com.company.gym.entity.Training;
import com.company.gym.exception.ValidationException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    private final TrainingDAO trainingDAO;
    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;
    private final Timer createTrainingTime;
    private final WorkloadClient workloadClient;

    public TrainingService(TrainingDAO trainingDAO, TraineeDAO traineeDAO, TrainerDAO trainerDAO, MeterRegistry meterRegistry, WorkloadClient workloadClient) {
        this.trainingDAO = trainingDAO;
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
        this.createTrainingTime = Timer.builder("app.training.creation.time")
                .description("Time taken to create a training session")
                .register(meterRegistry);
        this.workloadClient = workloadClient;
    }

    @Transactional
    public Training createTraining(String traineeUsername, String trainerUsername, String trainingName, Date trainingDate, Integer trainingDuration) {
        return createTrainingTime.record(() -> {
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
            TrainerWorkloadRequest workloadRequest = TrainerWorkloadRequest.builder()
                    .trainerUsername(trainer.getUser().getUsername())
                    .trainerFirstName(trainer.getUser().getFirstName())
                    .trainerLastName(trainer.getUser().getLastName())
                    .isActive(trainer.getUser().getIsActive())
                    .trainingDate(training.getTrainingDate())
                    .trainingDuration(training.getTrainingDuration())
                    .actionType(TrainerWorkloadRequest.ActionType.ADD)
                    .build();

            callWorkloadService(workloadRequest);
            logger.info("Training '{}' created for Trainee {} and Trainer {}.", trainingName, traineeUsername, trainerUsername);
            return training;
        });
    }

    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallbackWorkload")
    public void callWorkloadService(TrainerWorkloadRequest request) {
        workloadClient.updateWorkload(request);
    }

    public void fallbackWorkload(TrainerWorkloadRequest request, Throwable t) {
        logger.error("Failed to update workload service. Service unavailable. Reason: {}", t.getMessage());
    }


    @Transactional
    public void deleteTraining(Long trainingId) {
        Training training = trainingDAO.findById(trainingId);
        if (training != null) {
            TrainerWorkloadRequest workloadRequest = TrainerWorkloadRequest.builder()
                    .trainerUsername(training.getTrainer().getUser().getUsername())
                    .trainerFirstName(training.getTrainer().getUser().getFirstName())
                    .trainerLastName(training.getTrainer().getUser().getLastName())
                    .isActive(training.getTrainer().getUser().getIsActive())
                    .trainingDate(training.getTrainingDate())
                    .trainingDuration(training.getTrainingDuration())
                    .actionType(TrainerWorkloadRequest.ActionType.DELETE)
                    .build();

            trainingDAO.delete(training);
            callWorkloadService(workloadRequest);
        }
    }
}