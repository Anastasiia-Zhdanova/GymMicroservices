package com.company.gym.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Short DTO for a Trainee, used in Trainer profile's trainees list.")
public class TraineeShortResponse {

    @Schema(description = "Trainee's unique username.")
    private String username;

    @Schema(description = "Trainee's first name.")
    private String firstName;

    @Schema(description = "Trainee's last name.")
    private String lastName;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}