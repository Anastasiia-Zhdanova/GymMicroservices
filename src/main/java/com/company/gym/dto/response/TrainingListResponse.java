package com.company.gym.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "Response DTO for a single item in a Trainings List.")
public class TrainingListResponse {

    @Schema(description = "Name of the training.")
    private String trainingName;

    @Schema(description = "Date of the training.")
    private Date trainingDate;

    @Schema(description = "Type of the training (name).")
    private String trainingType;

    @Schema(description = "Duration of the training in minutes.")
    private Integer trainingDuration;

    @Schema(description = "Full name of the associated party (Trainer or Trainee).")
    private String associatedUserName;

    public String getTrainingName() { return trainingName; }
    public void setTrainingName(String trainingName) { this.trainingName = trainingName; }
    public Date getTrainingDate() { return trainingDate; }
    public void setTrainingDate(Date trainingDate) { this.trainingDate = trainingDate; }
    public String getTrainingType() { return trainingType; }
    public void setTrainingType(String trainingType) { this.trainingType = trainingType; }
    public Integer getTrainingDuration() { return trainingDuration; }
    public void setTrainingDuration(Integer trainingDuration) { this.trainingDuration = trainingDuration; }
    public String getAssociatedUserName() { return associatedUserName; }
    public void setAssociatedUserName(String associatedUserName) { this.associatedUserName = associatedUserName; }
}