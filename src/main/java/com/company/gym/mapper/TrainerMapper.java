package com.company.gym.mapper;

import com.company.gym.dto.response.*;
import com.company.gym.entity.Trainee;
import com.company.gym.entity.Trainer;
import com.company.gym.entity.Training;
import com.company.gym.entity.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class TrainerMapper {

    @Autowired
    protected TraineeMapper traineeMapper;

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "specialization", target = "specialization")
    @Mapping(source = "trainees", target = "traineesList", qualifiedByName = "mapTraineesSetToShortList")
    public abstract TrainerProfileResponse toTrainerProfileResponse(Trainer trainer);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "specialization.name", target = "specialization.trainingTypeName")
    @Mapping(source = "specialization.id", target = "specialization.id")
    public abstract TrainerShortResponse toTrainerShortResponse(Trainer trainer);
    public abstract List<TrainerShortResponse> toTrainerShortResponseList(List<Trainer> trainers);

    @Named("mapTraineesSetToShortList")
    public List<TraineeShortResponse> mapTraineesSetToShortList(Set<Trainee> trainees) {
        if (trainees == null) {
            return List.of();
        }
        return trainees.stream()
                .map(traineeMapper::toTraineeShortResponse)
                .collect(Collectors.toList());
    }

    @Mapping(source = "trainingName", target = "trainingName")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "trainingType.name", target = "trainingType")
    @Mapping(source = "trainingDuration", target = "trainingDuration")
    @Mapping(expression = "java(training.getTrainee().getUser().getFirstName() + ' ' + training.getTrainee().getUser().getLastName())",
            target = "associatedUserName")
    public abstract TrainingListResponse toTrainingListResponse(Training training);

    public List<TrainingListResponse> toTrainingListResponse(List<Training> trainings) {
        if (trainings == null) {
            return null;
        }
        return trainings.stream().map(this::toTrainingListResponse).toList();
    }

    @Mapping(source = "name", target = "trainingTypeName")
    public abstract TrainingTypeResponse toTrainingTypeResponse(TrainingType trainingType);
    public abstract List<TrainingTypeResponse> toTrainingTypeResponseList(List<TrainingType> trainingTypes);
}