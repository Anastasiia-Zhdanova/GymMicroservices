package com.company.gym.workload.dto;

import lombok.Data;
import java.util.Date;

@Data
public class TrainerWorkloadRequest {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean isActive;
    private Date trainingDate;
    private Integer trainingDuration;
    private ActionType actionType;

    public enum ActionType {
        ADD, DELETE
    }
}