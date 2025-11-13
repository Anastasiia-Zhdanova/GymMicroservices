package com.company.gym.controller;

import com.company.gym.dto.request.ChangePasswordRequest;
import com.company.gym.dto.request.LoginRequest;
import com.company.gym.dto.request.TraineeRegistrationRequest;
import com.company.gym.dto.request.TrainerRegistrationRequest;
import com.company.gym.dto.response.AuthResponse;
import com.company.gym.exception.AuthenticationException;
import com.company.gym.service.AuthService;
import com.company.gym.service.TraineeService;
import com.company.gym.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication and Registration")
public class AuthenticationController {

    private final AuthService authService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public AuthenticationController(AuthService authService,
                                    TraineeService traineeService,
                                    TrainerService trainerService) {
        this.authService = authService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    // №1 Trainee Registration - POST /api/v1/auth/trainee/register
    @PostMapping("/trainee/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "1. Trainee Registration", description = "Возвращает сгенерированные Username и Password.")
    public AuthResponse registerTrainee(@Valid @RequestBody TraineeRegistrationRequest request) {
        return traineeService.createProfile(
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress()
        );
    }

    // №2 Trainer Registration - POST /api/v1/auth/trainer/register
    @PostMapping("/trainer/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "2. Trainer Registration", description = "Возвращает сгенерированные Username и Password.")
    public AuthResponse registerTrainer(@Valid @RequestBody TrainerRegistrationRequest request) {
        return trainerService.createProfile(
                request.getFirstName(),
                request.getLastName(),
                request.getSpecializationId()
        );
    }

    // №3 Login - POST /api/v1/auth/login
    @PostMapping("/login")
    @Operation(summary = "3. Login", description = "Аутентификация через Spring Security Filter. Возвращает 200 OK при успехе.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
            @Content(schema = @Schema(implementation = LoginRequest.class))))
    public ResponseEntity<Void> login() {
        return ResponseEntity.ok().build();
    }

    // №4 Change Login (Password) - PUT /api/v1/auth/change-password
    @PutMapping("/change-password")
    @Operation(summary = "4. Change Login (Password)", description = "Требуется аутентификация. Idempotent.")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request,
            HttpSession session) {

        if (userDetails == null || !userDetails.getUsername().equals(request.getUsername())) {
            throw new AuthenticationException("Forbidden. You can only change your own password.");
        }

        authService.changePassword(
                request.getUsername(),
                request.getOldPassword(),
                request.getNewPassword()
        );

        SecurityContextHolder.clearContext();
        session.invalidate();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    @Operation(description = "Logout current user")
    @ResponseStatus(HttpStatus.OK)
    public void logout() {

    }
}