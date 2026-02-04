package com.company.gym.workload.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TrainerWorkloadTest {

    @Test
    void testModelStructure() {
        MonthSummary month = new MonthSummary(1, 100L);
        List<MonthSummary> months = new ArrayList<>();
        months.add(month);

        YearSummary year = new YearSummary(2026, months);
        List<YearSummary> years = new ArrayList<>();
        years.add(year);

        TrainerWorkload workload = new TrainerWorkload();
        workload.setUsername("trainer.test");
        workload.setFirstName("John");
        workload.setLastName("Doe");
        workload.setIsActive(true);
        workload.setYears(years);

        // Lombok Getters/Setters, equals/hashcode
        assertEquals("trainer.test", workload.getUsername());
        assertEquals(1, workload.getYears().size());
        assertEquals(2026, workload.getYears().get(0).getYear());

        assertNotNull(workload.toString());
    }
}