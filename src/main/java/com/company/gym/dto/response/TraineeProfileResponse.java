package com.company.gym.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@Schema(description = "Response DTO for Trainee profile details.")
public class TraineeProfileResponse {

    @Schema(description = "Trainee's unique username.")
    private String username;

    @Schema(description = "Trainee's first name.")
    private String firstName;

    @Schema(description = "Trainee's last name.")
    private String lastName;

    @Schema(description = "Trainee's date of birth.")
    private Date dateOfBirth;

    @Schema(description = "Trainee's address.")
    private String address;

    @Schema(description = "Trainee's active status.")
    private Boolean isActive;

    @Schema(description = "List of associated trainers.")
    private List<TrainerShortResponse> trainersList;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public List<TrainerShortResponse> getTrainersList() { return trainersList; }
    public void setTrainersList(List<TrainerShortResponse> trainersList) { this.trainersList = trainersList; }
}