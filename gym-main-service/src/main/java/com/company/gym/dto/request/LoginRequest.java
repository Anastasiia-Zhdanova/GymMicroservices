package com.company.gym.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request DTO for user login.")
public class LoginRequest {

    @NotBlank(message = "Username is required.")
    @Schema(description = "User's unique username.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Password is required.")
    @Schema(description = "User's password.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}