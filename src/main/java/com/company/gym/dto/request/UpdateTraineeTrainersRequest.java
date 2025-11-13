package com.company.gym.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

@Schema(description = "Request DTO for updating a Trainee's associated Trainer list.")
public class UpdateTraineeTrainersRequest {

    @NotNull(message = "Trainers list cannot be null.")
    @Schema(description = "Set of Trainer usernames to associate with the Trainee. Can be empty to clear the list.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<String> trainerUsernames;

    public Set<String> getTrainerUsernames() { return trainerUsernames; }
    public void setTrainerUsernames(Set<String> trainerUsernames) { this.trainerUsernames = trainerUsernames; }
}