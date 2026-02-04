package com.company.gym.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response DTO for Trainer profile details.")
public class TrainerProfileResponse {

    @Schema(description = "Trainer's unique username.")
    private String username;

    @Schema(description = "Trainer's first name.")
    private String firstName;

    @Schema(description = "Trainer's last name.")
    private String lastName;

    @Schema(description = "Trainer's specialization.")
    private TrainingTypeResponse specialization;

    @Schema(description = "Trainer's active status.")
    private Boolean isActive;

    @Schema(description = "List of associated trainees.")
    private List<TraineeShortResponse> traineesList;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public TrainingTypeResponse getSpecialization() { return specialization; }
    public void setSpecialization(TrainingTypeResponse specialization) { this.specialization = specialization; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public List<TraineeShortResponse> getTraineesList() { return traineesList; }
    public void setTraineesList(List<TraineeShortResponse> traineesList) { this.traineesList = traineesList; }
}