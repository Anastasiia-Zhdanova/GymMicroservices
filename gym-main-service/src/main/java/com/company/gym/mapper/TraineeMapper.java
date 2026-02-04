package com.company.gym.mapper;

import com.company.gym.dto.response.TraineeProfileResponse;
import com.company.gym.dto.response.TraineeShortResponse;
import com.company.gym.dto.response.TrainerShortResponse;
import com.company.gym.dto.response.TrainingListResponse;
import com.company.gym.entity.Trainee;
import com.company.gym.entity.Trainer;
import com.company.gym.entity.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class TraineeMapper {

    @Autowired @Lazy
    protected TrainerMapper trainerMapper;

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "trainers", target = "trainersList", qualifiedByName = "mapTrainersSetToShortList")
    public abstract TraineeProfileResponse toTraineeProfileResponse(Trainee trainee);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    public abstract TraineeShortResponse toTraineeShortResponse(Trainee trainee);

    public abstract List<TraineeShortResponse> toTraineeShortResponseList(List<Trainee> trainees);

    @Named("mapTrainersSetToShortList")
    public List<TrainerShortResponse> mapTrainersSetToShortList(Set<Trainer> trainers) {
        if (trainers == null) {
            return List.of();
        }
        return trainers.stream()
                .map(trainerMapper::toTrainerShortResponse)
                .collect(Collectors.toList());
    }

    @Mapping(source = "trainingName", target = "trainingName")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "trainingType.name", target = "trainingType")
    @Mapping(source = "trainingDuration", target = "trainingDuration")
    @Mapping(expression = "java(training.getTrainer().getUser().getFirstName() + ' ' + training.getTrainer().getUser().getLastName())",
            target = "associatedUserName")
    public abstract TrainingListResponse toTrainingListResponse(Training training);

    public List<TrainingListResponse> toTrainingListResponse(List<Training> trainings) {
        if (trainings == null) {
            return null;
        }
        return trainings.stream().map(this::toTrainingListResponse).toList();
    }
}