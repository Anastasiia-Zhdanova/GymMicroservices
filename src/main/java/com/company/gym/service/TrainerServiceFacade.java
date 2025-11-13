package com.company.gym.service;

import com.company.gym.dto.request.TrainerProfileUpdateRequest;
import com.company.gym.dto.request.UserStatusUpdateRequest;
import com.company.gym.dto.response.TrainerProfileResponse;
import com.company.gym.dto.response.TrainerShortResponse;
import com.company.gym.dto.response.TrainingListResponse;
import com.company.gym.dto.response.TrainingTypeResponse;
import com.company.gym.mapper.TrainerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class TrainerServiceFacade {
    private final TrainerService trainerService;
    private final TrainerMapper trainerMapper;
    private final TrainingTypeService trainingTypeService;

    public TrainerServiceFacade(TrainerService trainerService,
                                TrainerMapper trainerMapper,
                                TrainingTypeService trainingTypeService) {
        this.trainerService = trainerService;
        this.trainerMapper = trainerMapper;
        this.trainingTypeService = trainingTypeService;
    }

    public TrainerProfileResponse getProfile(
            String username
    ) {
        var trainer = trainerService.selectProfile(username);
        return trainerMapper.toTrainerProfileResponse(trainer);
    }

    @Transactional
    public TrainerProfileResponse updateProfile(
            String username,
            TrainerProfileUpdateRequest request
    ) {
        var updatedTrainer = trainerService.updateProfile(
                username,
                request.getFirstName(),
                request.getLastName(),
                request.getSpecializationId()
        );

        trainerService.activateDeactivateProfile(username, request.getIsActive());
        updatedTrainer.getUser().setIsActive(request.getIsActive());

        return trainerMapper.toTrainerProfileResponse(updatedTrainer);
    }

    public List<TrainerShortResponse> getUnassignedTrainers(
            String traineeUsername
    ) {
        var unassignedTrainers = trainerService.getUnassignedTrainers(traineeUsername);
        return trainerMapper.toTrainerShortResponseList(unassignedTrainers);
    }

    public List<TrainingListResponse> getTrainings(
            String username,
            Date fromDate,
            Date toDate
    ) {
        var trainings = trainerService.getTrainerTrainingsList(username, fromDate, toDate);
        return trainerMapper.toTrainingListResponse(trainings);
    }

    public void updateStatus(
            String username,
            UserStatusUpdateRequest request
    ) {
        trainerService.activateDeactivateProfile(username, request.getIsActive());
    }

    public List<TrainingTypeResponse> getAllTrainingTypes() {
        var types = trainingTypeService.getAllTrainingTypes();
        return trainerMapper.toTrainingTypeResponseList(types);
    }
}
