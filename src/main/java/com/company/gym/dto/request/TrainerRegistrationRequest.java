package com.company.gym.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request DTO for Trainer registration.")
public class TrainerRegistrationRequest {

    @NotBlank(message = "First name is required.")
    @Schema(description = "Trainer's first name.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Schema(description = "Trainer's last name.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @NotNull(message = "Specialization ID is required.")
    @Schema(description = "ID of the Trainer's specialization (Training Type).", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long specializationId;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Long getSpecializationId() { return specializationId; }
    public void setSpecializationId(Long specializationId) { this.specializationId = specializationId; }
}