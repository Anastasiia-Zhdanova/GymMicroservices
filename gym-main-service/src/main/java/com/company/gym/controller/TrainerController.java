package com.company.gym.controller;

import com.company.gym.dto.request.TrainerProfileUpdateRequest;
import com.company.gym.dto.request.UserStatusUpdateRequest;
import com.company.gym.dto.response.TrainerProfileResponse;
import com.company.gym.dto.response.TrainerShortResponse;
import com.company.gym.dto.response.TrainingListResponse;
import com.company.gym.dto.response.TrainingTypeResponse;
import com.company.gym.exception.AuthenticationException;
import com.company.gym.service.TrainerServiceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Trainer Profile and Training Types")
public class TrainerController {

    private final TrainerServiceFacade trainerService;

    public TrainerController(TrainerServiceFacade trainerService) {
        this.trainerService = trainerService;
    }

    private void checkAuthorization(String requestedUsername, UserDetails principal) {
        if (principal == null || !principal.getUsername().equals(requestedUsername)) {
            throw new AuthenticationException("Access denied. You can only manage your own profile.");
        }
    }

    // №8 Get Trainer Profile - GET /api/v1/trainers/{username}
    @GetMapping("/trainers/{username}")
    @Operation(summary = "8. Get Trainer Profile", description = "Требуется аутентификация. Idempotent.")
    public TrainerProfileResponse getProfile(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails principal
    ) {
        checkAuthorization(username, principal);
        return trainerService.getProfile(username);
    }

    // №9 Update Trainer Profile - PUT /api/v1/trainers/{username}
    @PutMapping("/trainers/{username}")
    @Operation(summary = "9. Update Trainer Profile", description = "Требуется аутентификация. Specialization - read-only. Idempotent.")
    public TrainerProfileResponse updateProfile(
            @PathVariable String username,
            @Valid @RequestBody TrainerProfileUpdateRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        checkAuthorization(username, principal);
        return trainerService.updateProfile(username, request);
    }

    // №10 Get not assigned active trainers - GET /api/v1/trainees/{traineeUsername}/unassigned-trainers
    @GetMapping("/trainees/{traineeUsername}/unassigned-trainers")
    @Operation(summary = "10. Get not assigned active trainers", description = "Требуется аутентификация клиента. Idempotent.")
    public List<TrainerShortResponse> getUnassignedTrainers(
            @PathVariable String traineeUsername,
            @AuthenticationPrincipal UserDetails principal
    ) {
        if (principal == null || !principal.getUsername().equals(traineeUsername)) {
            throw new AuthenticationException("Access denied. You can only view unassigned trainers for your own profile.");
        }
        return trainerService.getUnassignedTrainers(traineeUsername);
    }

    // №13 Get Trainer Trainings List - GET /api/v1/trainers/{username}/trainings
    @GetMapping("/trainers/{username}/trainings")
    @Operation(summary = "13. Get Trainer Trainings List", description = "Требуется аутентификация. Idempotent.")
    public List<TrainingListResponse> getTrainings(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,
            @AuthenticationPrincipal UserDetails principal
    ) {
        checkAuthorization(username, principal);
        return trainerService.getTrainings(username, fromDate, toDate);
    }

    // №16 Activate/De-Activate Trainer - PATCH /api/v1/trainers/{username}/status
    @PatchMapping("/trainers/{username}/status")
    @Operation(summary = "16. Activate/De-Activate Trainer", description = "Требуется аутентификация. Non-Idempotent.")
    public void updateStatus(
            @PathVariable String username,
            @Valid @RequestBody UserStatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        checkAuthorization(username, principal);
        trainerService.updateStatus(username, request);
    }

    // №17 Get Training types - GET /api/v1/training-types
    @GetMapping("/training-types")
    @Operation(summary = "17. Get Training types", description = "Не требует аутентификации. Idempotent.")
    public List<TrainingTypeResponse> getAllTrainingTypes() {
        return trainerService.getAllTrainingTypes();
    }
}