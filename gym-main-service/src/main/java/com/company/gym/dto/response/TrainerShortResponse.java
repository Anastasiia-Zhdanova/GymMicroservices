package com.company.gym.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Short DTO for a Trainer, used in Trainee profile's trainers list.")
public class TrainerShortResponse {

    @Schema(description = "Trainer's unique username.")
    private String username;

    @Schema(description = "Trainer's first name.")
    private String firstName;

    @Schema(description = "Trainer's last name.")
    private String lastName;

    @Schema(description = "Trainer's specialization.")
    private TrainingTypeResponse specialization;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public TrainingTypeResponse getSpecialization() { return specialization; }
    public void setSpecialization(TrainingTypeResponse specialization) { this.specialization = specialization; }
}