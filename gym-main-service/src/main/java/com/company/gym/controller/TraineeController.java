package com.company.gym.controller;

import com.company.gym.dto.request.TraineeProfileUpdateRequest;
import com.company.gym.dto.request.UpdateTraineeTrainersRequest;
import com.company.gym.dto.request.UserStatusUpdateRequest;
import com.company.gym.dto.response.TraineeProfileResponse;
import com.company.gym.dto.response.TrainerShortResponse;
import com.company.gym.dto.response.TrainingListResponse;
import com.company.gym.exception.AuthenticationException;
import com.company.gym.service.TraineeServiceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trainees")
@Tag(name = "Trainee Profile Management")
public class TraineeController {
    private final TraineeServiceFacade traineeService;

    public TraineeController(TraineeServiceFacade traineeService) {
        this.traineeService = traineeService;
    }

    private void checkAuthorization(String requestedUsername, UserDetails principal) {
        if (principal == null || !principal.getUsername().equals(requestedUsername)) {
            throw new AuthenticationException("Access denied. You can only manage your own profile.");
        }
    }

    // №5 Get Trainee Profile - GET /api/v1/trainees/{username}
    @GetMapping("/{username}")
    @Operation(summary = "5. Get Trainee Profile", description = "Требуется аутентификация. Idempotent.")
    public TraineeProfileResponse getProfile(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails principal
    ) {
        checkAuthorization(username, principal);
        return traineeService.getProfile(username);
    }

    // №6 Update Trainee Profile - PUT /api/v1/trainees/{username}
    @PutMapping("/{username}")
    @Operation(summary = "6. Update Trainee Profile", description = "Требуется аутентификация. Idempotent.")
    public TraineeProfileResponse updateProfile(
            @PathVariable String username,
            @Valid @RequestBody TraineeProfileUpdateRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        checkAuthorization(username, principal);
        return traineeService.updateProfile(username, request);
    }

    // №7 Delete Trainee Profile - DELETE /api/v1/trainees/{username}
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content
    @Operation(summary = "7. Delete Trainee Profile", description ="Требуется аутентификация. Hard Delete (Cascade). Idempotent.")
    public ResponseEntity<Void> deleteProfile(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails principal
    ) {
        checkAuthorization(username, principal);
        return traineeService.deleteProfile(username);
    }

    // №11 Update Trainee's Trainer List - PUT /api/v1/trainees/{username}/trainers
    @PutMapping("/{username}/trainers")
    @Operation(summary = "11. Update Trainee's Trainer List", description ="Требуется аутентификация. Idempotent (полная замена списка).")
    public List<TrainerShortResponse> updateTrainers(
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeTrainersRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        checkAuthorization(username, principal);
        return traineeService.updateTrainers(username, request);
    }

    // №12 Get Trainee Trainings List - GET /api/v1/trainees/{username}/trainings
    @GetMapping("/{username}/trainings")
    @Operation(summary = "12. Get Trainee Trainings List", description ="Требуется аутентификация. Idempotent.")
    public List<TrainingListResponse> getTrainings(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingTypeName,
            @AuthenticationPrincipal UserDetails principal
    ) {
        checkAuthorization(username, principal);
        return traineeService.getTrainings(username, fromDate, toDate, trainerName, trainingTypeName);
    }

    // №15 Activate/De-Activate Trainee - PATCH /api/v1/trainees/{username}/status
    @PatchMapping("/{username}/status")
    @Operation(summary = "15. Activate/De-Activate Trainee", description ="Требуется аутентификация. Non-Idempotent (изменение статуса на основе тела запроса).")
    public void updateStatus(
            @PathVariable String username,
            @Valid @RequestBody UserStatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        checkAuthorization(username, principal);
        traineeService.updateStatus(username, request);
    }
}