package com.company.gym.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainingRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private TrainingRequest createValidRequest() {
        TrainingRequest request = new TrainingRequest();
        request.setTraineeUsername("trainee.user");
        request.setTrainerUsername("trainer.user");
        request.setTrainingName("Yoga Session");
        Date tomorrow = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        request.setTrainingDate(tomorrow);
        request.setTrainingDuration(60);

        return request;
    }

    @Test
    void testValidTrainingRequest_NoViolations() {
        TrainingRequest request = createValidRequest();
        assertTrue(validator.validate(request).isEmpty(), "A valid request should have no violations.");
    }

    @Test
    void testGettersAndSetters() {
        TrainingRequest request = new TrainingRequest();
        request.setTraineeUsername("t1");
        request.setTrainerUsername("t2");
        request.setTrainingName("Test");
        Date date = new Date();
        request.setTrainingDate(date);
        request.setTrainingDuration(30);

        assertEquals("t1", request.getTraineeUsername());
        assertEquals("t2", request.getTrainerUsername());
        assertEquals("Test", request.getTrainingName());
        assertEquals(date, request.getTrainingDate());
        assertEquals(30, request.getTrainingDuration());
    }

    @Test
    void testNotBlankViolations() {
        TrainingRequest request = createValidRequest();

        request.setTraineeUsername("");
        request.setTrainerUsername(" ");
        request.setTrainingName(null);

        assertEquals(3, validator.validate(request).size(), "Should fail on 3 @NotBlank violations.");
    }

    @Test
    void testTrainingDateViolations() {
        TrainingRequest request = createValidRequest();

        request.setTrainingDate(null);
        assertEquals(1, validator.validate(request).stream()
                .filter(v -> v.getPropertyPath().toString().equals("trainingDate"))
                .count(), "@NotNull violation expected for trainingDate.");
        request.setTrainingDate(createValidRequest().getTrainingDate());

        Date yesterday = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));
        request.setTrainingDate(yesterday);
        assertEquals(1, validator.validate(request).stream()
                .filter(v -> v.getPropertyPath().toString().equals("trainingDate"))
                .count(), "@FutureOrPresent violation expected for past date.");
    }

    @Test
    void testTrainingDurationViolations() {
        TrainingRequest request = createValidRequest();

        request.setTrainingDuration(null);
        assertEquals(1, validator.validate(request).stream()
                .filter(v -> v.getPropertyPath().toString().equals("trainingDuration"))
                .count(), "@NotNull violation expected for trainingDuration.");
        request.setTrainingDuration(createValidRequest().getTrainingDuration());

        request.setTrainingDuration(0);
        assertEquals(1, validator.validate(request).stream()
                .filter(v -> v.getPropertyPath().toString().equals("trainingDuration"))
                .count(), "@Min(1) violation expected for duration = 0.");

        request.setTrainingDuration(-10);
        assertEquals(1, validator.validate(request).stream()
                .filter(v -> v.getPropertyPath().toString().equals("trainingDuration"))
                .count(), "@Min(1) violation expected for negative duration.");
    }
}