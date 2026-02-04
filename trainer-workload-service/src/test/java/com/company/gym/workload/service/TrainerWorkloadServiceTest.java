package com.company.gym.workload.service;

import com.company.gym.workload.dto.TrainerWorkloadRequest;
import com.company.gym.workload.model.MonthSummary;
import com.company.gym.workload.model.TrainerWorkload;
import com.company.gym.workload.model.YearSummary;
import com.company.gym.workload.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceTest {

    @Mock
    private TrainerWorkloadRepository repository;

    @InjectMocks
    private TrainerWorkloadService service;

    @Test
    void shouldCreateNewWorkloadIfNoneExists() {
        // Given
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("new.trainer");
        request.setTrainerFirstName("New");
        request.setTrainerLastName("Guy");
        request.setIsActive(true);
        request.setTrainingDate(new Date()); // Current date
        request.setTrainingDuration(60);
        request.setActionType(TrainerWorkloadRequest.ActionType.ADD);

        when(repository.findByUsername("new.trainer")).thenReturn(Optional.empty());

        // When
        service.updateWorkload(request);

        // Then
        verify(repository).save(any(TrainerWorkload.class));
    }

    @Test
    void shouldUpdateExistingWorkloadAddDuration() {
        // Given
        MonthSummary month = new MonthSummary(1, 100L); // Январь
        List<MonthSummary> months = new ArrayList<>();
        months.add(month);

        YearSummary year = new YearSummary(2026, months);
        List<YearSummary> years = new ArrayList<>();
        years.add(year);

        TrainerWorkload existing = new TrainerWorkload("trainer.exist", "A", "B", true, years);

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("trainer.exist");
        request.setTrainingDuration(60);
        request.setActionType(TrainerWorkloadRequest.ActionType.ADD);
        request.setTrainingDate(new Date(1768464000000L)); // Timestamp for 2026-01-15

        when(repository.findByUsername("trainer.exist")).thenReturn(Optional.of(existing));

        // When
        service.updateWorkload(request);

        // Then
        verify(repository).save(existing);
    }
}