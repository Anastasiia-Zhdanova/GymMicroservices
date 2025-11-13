package com.company.gym.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO for a single Training Type or Specialization.")
public class TrainingTypeResponse {

    @Schema(description = "Training type name.")
    private String trainingTypeName;

    @Schema(description = "Training type ID.")
    private Long id;

    public TrainingTypeResponse() {}

    public TrainingTypeResponse(String trainingTypeName, Long id) {
        this.trainingTypeName = trainingTypeName;
        this.id = id;
    }

    public String getTrainingTypeName() { return trainingTypeName; }
    public void setTrainingTypeName(String trainingTypeName) { this.trainingTypeName = trainingTypeName; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}