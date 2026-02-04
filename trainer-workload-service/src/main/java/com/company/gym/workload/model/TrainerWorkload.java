package com.company.gym.workload.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trainer_workload")
public class TrainerWorkload {
    @Id
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private List<YearSummary> years;
}