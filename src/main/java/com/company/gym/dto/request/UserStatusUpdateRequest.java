package com.company.gym.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request DTO for activating or deactivating a user profile.")
public class UserStatusUpdateRequest {

    @NotNull(message = "Is Active status is required.")
    @Schema(description = "Target active status (true for activation, false for deactivation).", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isActive;

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}