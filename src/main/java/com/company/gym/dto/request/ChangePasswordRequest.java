package com.company.gym.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request DTO for changing user password.")
public class ChangePasswordRequest {

    @NotBlank(message = "Username is required.")
    @Schema(description = "Username of the account to update.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Old Password is required.")
    @Schema(description = "Current password.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;

    @NotBlank(message = "New Password is required.")
    @Size(min = 8, message = "New password must be at least 8 characters long.")
    @Schema(description = "New password.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}