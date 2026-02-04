package com.company.gym.workload.service;

import com.company.gym.workload.dto.TrainerWorkloadRequest;
import com.company.gym.workload.model.MonthSummary;
import com.company.gym.workload.model.TrainerWorkload;
import com.company.gym.workload.model.YearSummary;
import com.company.gym.workload.repository.TrainerWorkloadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadService {

    private final TrainerWorkloadRepository repository;

    public void updateWorkload(TrainerWorkloadRequest request) {
        log.info("Updating workload for trainer: {}. Action: {}", request.getTrainerUsername(), request.getActionType());

        TrainerWorkload workload = repository.findByUsername(request.getTrainerUsername())
                .orElseGet(() -> {
                    log.info("Creating new workload profile for {}", request.getTrainerUsername());
                    return new TrainerWorkload(
                            request.getTrainerUsername(),
                            request.getTrainerFirstName(),
                            request.getTrainerLastName(),
                            request.getIsActive(),
                            new ArrayList<>()
                    );
                });

        LocalDate date = request.getTrainingDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = date.getYear();
        int month = date.getMonthValue();

        YearSummary yearSummary = workload.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    YearSummary newYear = new YearSummary(year, new ArrayList<>());
                    workload.getYears().add(newYear);
                    return newYear;
                });

        MonthSummary monthSummary = yearSummary.getMonths().stream()
                .filter(m -> m.getMonthValue() == month)
                .findFirst()
                .orElseGet(() -> {
                    MonthSummary newMonth = new MonthSummary(month, 0L);
                    yearSummary.getMonths().add(newMonth);
                    return newMonth;
                });

        long newDuration = monthSummary.getTotalDuration();
        if (request.getActionType() == TrainerWorkloadRequest.ActionType.ADD) {
            newDuration += request.getTrainingDuration();
        } else {
            newDuration -= request.getTrainingDuration();
            if (newDuration < 0) newDuration = 0;
        }
        monthSummary.setTotalDuration(newDuration);

        if (newDuration == 0) {
            yearSummary.getMonths().remove(monthSummary);
        }
        if (yearSummary.getMonths().isEmpty()) {
            workload.getYears().remove(yearSummary);
        }

        repository.save(workload);
        log.info("Workload updated successfully.");
    }
}