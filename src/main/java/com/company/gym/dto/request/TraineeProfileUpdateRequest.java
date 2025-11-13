package com.company.gym.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.util.Date;

@Schema(description = "Request DTO for updating Trainee profile information.")
public class TraineeProfileUpdateRequest {

    @NotBlank(message = "First name is required.")
    @Schema(description = "New first name.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Schema(description = "New last name.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @PastOrPresent(message = "Date of birth must be in the past.")
    @Schema(description = "New date of birth.")
    private Date dateOfBirth;

    @Schema(description = "New address.")
    private String address;

    @NotNull(message = "Is Active status is required.")
    @Schema(description = "New active status (true/false).", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isActive;

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
}