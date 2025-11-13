package com.company.gym.service;

import com.company.gym.dto.request.TraineeProfileUpdateRequest;
import com.company.gym.dto.request.UpdateTraineeTrainersRequest;
import com.company.gym.dto.request.UserStatusUpdateRequest;
import com.company.gym.dto.response.TraineeProfileResponse;
import com.company.gym.dto.response.TrainerShortResponse;
import com.company.gym.dto.response.TrainingListResponse;
import com.company.gym.mapper.TraineeMapper;
import com.company.gym.mapper.TrainerMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class TraineeServiceFacade {
    private final TraineeService traineeService;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;

    public TraineeServiceFacade(TraineeService traineeService,
                                TraineeMapper traineeMapper,
                                TrainerMapper trainerMapper) {
        this.traineeService = traineeService;
        this.traineeMapper = traineeMapper;
        this.trainerMapper = trainerMapper;
    }

    public TraineeProfileResponse getProfile(
            String username
    ) {
        var trainee = traineeService.selectProfile(username);
        return traineeMapper.toTraineeProfileResponse(trainee);
    }

    @Transactional
    public TraineeProfileResponse updateProfile(
            String username,
            TraineeProfileUpdateRequest request
    ) {
        var updatedTrainee = traineeService.updateProfile(
                username,
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress()
        );

        traineeService.activateDeactivateProfile(username, request.getIsActive());
        updatedTrainee.getUser().setIsActive(request.getIsActive());

        return traineeMapper.toTraineeProfileResponse(updatedTrainee);
    }

    public ResponseEntity<Void> deleteProfile(
            String username
    ) {
        traineeService.deleteProfile(username);
        return ResponseEntity.noContent().build();
    }

    public List<TrainerShortResponse> updateTrainers(
            String username,
            UpdateTraineeTrainersRequest request
    ) {
        var updatedTrainee = traineeService.updateTrainersList(username, request.getTrainerUsernames());
        return updatedTrainee.getTrainers().stream()
                .map(trainerMapper::toTrainerShortResponse)
                .toList();
    }

    public List<TrainingListResponse> getTrainings(
            String username,
            Date fromDate,
            Date toDate,
            String trainerName,
            String trainingTypeName
    ) {
        var trainings = traineeService.getTraineeTrainingsList(username, fromDate, toDate, trainerName, trainingTypeName);
        return traineeMapper.toTrainingListResponse(trainings);
    }

    public void updateStatus(String username, UserStatusUpdateRequest request) {
        traineeService.activateDeactivateProfile(username, request.getIsActive());
    }
}
