package com.company.gym.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Schema(description = "Request DTO for creating a new Training session.")
public class TrainingRequest {

    @NotBlank(message = "Trainee username is required.")
    @Schema(description = "Username of the Trainee.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String traineeUsername;

    @NotBlank(message = "Trainer username is required.")
    @Schema(description = "Username of the Trainer.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String trainerUsername;

    @NotBlank(message = "Training name is required.")
    @Schema(description = "Name of the training session.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String trainingName;

    @NotNull(message = "Training date is required.")
    @FutureOrPresent(message = "Training date cannot be in the past.")
    @Schema(description = "Date of the training.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date trainingDate;

    @NotNull(message = "Training duration is required.")
    @Min(value = 1, message = "Training duration must be a positive number.")
    @Schema(description = "Duration of the training in minutes.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer trainingDuration;

    public String getTraineeUsername() { return traineeUsername; }
    public void setTraineeUsername(String traineeUsername) { this.traineeUsername = traineeUsername; }
    public String getTrainerUsername() { return trainerUsername; }
    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }
    public String getTrainingName() { return trainingName; }
    public void setTrainingName(String trainingName) { this.trainingName = trainingName; }
    public Date getTrainingDate() { return trainingDate; }
    public void setTrainingDate(Date trainingDate) { this.trainingDate = trainingDate; }
    public Integer getTrainingDuration() { return trainingDuration; }
    public void setTrainingDuration(Integer trainingDuration) { this.trainingDuration = trainingDuration; }
}