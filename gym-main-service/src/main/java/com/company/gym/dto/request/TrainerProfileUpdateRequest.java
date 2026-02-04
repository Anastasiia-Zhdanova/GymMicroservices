package com.company.gym.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request DTO for updating Trainer profile information.")
public class TrainerProfileUpdateRequest {

    @NotBlank(message = "First name is required.")
    @Schema(description = "New first name.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Schema(description = "New last name.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @Schema(description = "Trainer's specialization ID (read only).")
    private Long specializationId;

    @NotNull(message = "Is Active status is required.")
    @Schema(description = "New active status (true/false).", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isActive;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Long getSpecializationId() { return specializationId; }
    public void setSpecializationId(Long specializationId) { this.specializationId = specializationId; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}