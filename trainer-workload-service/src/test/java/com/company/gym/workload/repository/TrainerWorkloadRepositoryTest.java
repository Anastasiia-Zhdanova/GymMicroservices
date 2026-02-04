package com.company.gym.workload.repository;

import com.company.gym.workload.model.TrainerWorkload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class TrainerWorkloadRepositoryTest {

    @Autowired
    private TrainerWorkloadRepository repository;

    @Test
    void shouldSaveAndFindWorkload() {
        // Given
        TrainerWorkload workload = new TrainerWorkload(
                "trainer.test", "Test", "Trainer", true, new ArrayList<>()
        );

        // When
        repository.save(workload);
        Optional<TrainerWorkload> found = repository.findByUsername("trainer.test");

        // Then
        assertTrue(found.isPresent());
        assertEquals("trainer.test", found.get().getUsername());
    }
}