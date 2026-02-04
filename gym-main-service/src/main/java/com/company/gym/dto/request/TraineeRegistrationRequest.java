package com.company.gym.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Schema(description = "Request DTO for Trainee registration.")
public class TraineeRegistrationRequest {

    @NotBlank(message = "First name is required.")
    @Schema(description = "Trainee's first name.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Schema(description = "Trainee's last name.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @PastOrPresent(message = "Date of birth cannot be in the future.")
    @Schema(description = "Trainee's date of birth.")
    private Date dateOfBirth;

    @Size(max = 255, message = "Address length cannot exceed 255 characters.")
    @Schema(description = "Trainee's address.")
    private String address;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}