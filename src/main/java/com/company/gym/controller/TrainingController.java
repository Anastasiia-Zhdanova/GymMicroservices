package com.company.gym.controller;

import com.company.gym.dto.request.TrainingRequest;
import com.company.gym.exception.AuthenticationException;
import com.company.gym.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trainings")
@Tag(name = "Training Management")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    // №14 Add Training - POST /api/v1/trainings
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary= "14. Add Training", description = "Требуется аутентификация. Non-Idempotent. Аутентифицированный пользователь должен быть Trainee или Trainer из запроса.")
    public ResponseEntity<Void> createTraining(
            @Valid @RequestBody TrainingRequest request,
            @AuthenticationPrincipal UserDetails principal // Logged-in username from Redis Session
    ) {
        if (principal == null) {
            throw new AuthenticationException("Authentication required.");
        }

        String principalUsername = principal.getUsername();

        if (!principalUsername.equals(request.getTraineeUsername()) && !principalUsername.equals(request.getTrainerUsername())) {
            throw new AuthenticationException("Access denied. Training must be created by one of the involved parties (Trainee or Trainer).");
        }

        trainingService.createTraining(
                request.getTraineeUsername(),
                request.getTrainerUsername(),
                request.getTrainingName(),
                request.getTrainingDate(),
                request.getTrainingDuration()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}