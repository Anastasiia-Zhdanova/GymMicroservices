package com.company.gym.service;

import com.company.gym.dao.TraineeDAO;
import com.company.gym.dao.TrainerDAO;
import com.company.gym.dao.TrainingDAO;
import com.company.gym.entity.Trainee;
import com.company.gym.entity.Trainer;
import com.company.gym.entity.Training;
import com.company.gym.entity.TrainingType;
import com.company.gym.exception.ValidationException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest {

    @Mock
    private TrainingDAO trainingDAO;
    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private TrainerDAO trainerDAO;

    private MeterRegistry meterRegistry;
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        trainingService = new TrainingService(trainingDAO, traineeDAO, trainerDAO, meterRegistry);
    }

    @Test
    void testCreateTraining_Success() {
        String traineeUsername = "trainee.user";
        String trainerUsername = "trainer.user";
        String trainingName = "Morning Workout";
        Date trainingDate = new Date();
        Integer trainingDuration = 60;

        Trainee mockTrainee = mock(Trainee.class);
        Trainer mockTrainer = mock(Trainer.class);
        TrainingType mockSpecialization = mock(TrainingType.class);
        Set<Trainer> trainerSet = new HashSet<>();
        trainerSet.add(mockTrainer);

        when(traineeDAO.findByUsernameWithTrainers(traineeUsername)).thenReturn(mockTrainee);
        when(trainerDAO.findByUsername(trainerUsername)).thenReturn(mockTrainer);
        when(mockTrainee.getTrainers()).thenReturn(trainerSet);
        when(mockTrainer.getSpecialization()).thenReturn(mockSpecialization);

        Training result = trainingService.createTraining(traineeUsername, trainerUsername, trainingName, trainingDate, trainingDuration);

        assertNotNull(result);

        ArgumentCaptor<Training> trainingCaptor = ArgumentCaptor.forClass(Training.class);
        verify(trainingDAO).save(trainingCaptor.capture());

        Training savedTraining = trainingCaptor.getValue();
        assertEquals(mockTrainee, savedTraining.getTrainee());
        assertEquals(mockTrainer, savedTraining.getTrainer());
        assertEquals(trainingName, savedTraining.getTrainingName());
        assertEquals(trainingDate, savedTraining.getTrainingDate());
        assertEquals(trainingDuration, savedTraining.getTrainingDuration());
        assertEquals(mockSpecialization, savedTraining.getTrainingType());

        assertEquals(1, meterRegistry.get("app.training.creation.time").timer().count());
    }

    @Test
    void testCreateTraining_NullInputs_ThrowsValidationException() {
        Date date = new Date();
        String trainee = "trainee";
        String trainer = "trainer";
        String name = "name";

        ValidationException e1 = assertThrows(ValidationException.class, () ->
                trainingService.createTraining(null, trainer, name, date, 60));
        assertTrue(e1.getMessage().contains("All fields"));

        ValidationException e2 = assertThrows(ValidationException.class, () ->
                trainingService.createTraining(trainee, null, name, date, 60));
        assertTrue(e2.getMessage().contains("All fields"));

        ValidationException e3 = assertThrows(ValidationException.class, () ->
                trainingService.createTraining(trainee, trainer, null, date, 60));
        assertTrue(e3.getMessage().contains("All fields"));

        ValidationException e4 = assertThrows(ValidationException.class, () ->
                trainingService.createTraining(trainee, trainer, name, null, 60));
        assertTrue(e4.getMessage().contains("All fields"));

        ValidationException e5 = assertThrows(ValidationException.class, () ->
                trainingService.createTraining(trainee, trainer, name, date, null));
        assertTrue(e5.getMessage().contains("All fields"));

        verify(trainingDAO, never()).save(any());

        assertEquals(5, meterRegistry.get("app.training.creation.time").timer().count());
    }

    @Test
    void testCreateTraining_InvalidDuration_ThrowsValidationException() {
        Date date = new Date();

        ValidationException e1 = assertThrows(ValidationException.class, () ->
                trainingService.createTraining("t", "tr", "n", date, 0));
        assertTrue(e1.getMessage().contains("duration must be positive"));

        ValidationException e2 = assertThrows(ValidationException.class, () ->
                trainingService.createTraining("t", "tr", "n", date, -10));
        assertTrue(e2.getMessage().contains("duration must be positive"));

        verify(trainingDAO, never()).save(any());

        assertEquals(2, meterRegistry.get("app.training.creation.time").timer().count());
    }

    @Test
    void testCreateTraining_TraineeNotFound_ThrowsValidationException() {
        String traineeUsername = "unknown.trainee";
        when(traineeDAO.findByUsernameWithTrainers(traineeUsername)).thenReturn(null);

        ValidationException e = assertThrows(ValidationException.class, () ->
                trainingService.createTraining(traineeUsername, "trainer", "name", new Date(), 60));

        assertEquals("Trainee not found with username: " + traineeUsername, e.getMessage());
        verify(trainingDAO, never()).save(any());
        assertEquals(1, meterRegistry.get("app.training.creation.time").timer().count());
    }

    @Test
    void testCreateTraining_TrainerNotFound_ThrowsValidationException() {
        String traineeUsername = "trainee.user";
        String trainerUsername = "unknown.trainer";
        Trainee mockTrainee = mock(Trainee.class);

        when(traineeDAO.findByUsernameWithTrainers(traineeUsername)).thenReturn(mockTrainee);
        when(trainerDAO.findByUsername(trainerUsername)).thenReturn(null);

        ValidationException e = assertThrows(ValidationException.class, () ->
                trainingService.createTraining(traineeUsername, trainerUsername, "name", new Date(), 60));

        assertEquals("Trainer not found with username: " + trainerUsername, e.getMessage());
        verify(trainingDAO, never()).save(any());
        assertEquals(1, meterRegistry.get("app.training.creation.time").timer().count());
    }

    @Test
    void testCreateTraining_TrainerNotAssociatedWithTrainee_ThrowsValidationException() {
        String traineeUsername = "trainee.user";
        String trainerUsername = "trainer.user";

        Trainee mockTrainee = mock(Trainee.class);
        Trainer mockTrainer = mock(Trainer.class);

        when(mockTrainee.getTrainers()).thenReturn(new HashSet<>());

        when(traineeDAO.findByUsernameWithTrainers(traineeUsername)).thenReturn(mockTrainee);
        when(trainerDAO.findByUsername(trainerUsername)).thenReturn(mockTrainer);

        ValidationException e = assertThrows(ValidationException.class, () ->
                trainingService.createTraining(traineeUsername, trainerUsername, "name", new Date(), 60));

        assertEquals("Trainer '" + trainerUsername + "' is not associated with Trainee '" + traineeUsername + "'.", e.getMessage());
        verify(trainingDAO, never()).save(any());
        assertEquals(1, meterRegistry.get("app.training.creation.time").timer().count());
    }
}