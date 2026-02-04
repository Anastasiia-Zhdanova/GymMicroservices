package com.company.gym.service;

import com.company.gym.client.WorkloadClient;
import com.company.gym.dao.TraineeDAO;
import com.company.gym.dao.TrainerDAO;
import com.company.gym.dao.TrainingDAO;
import com.company.gym.dto.request.TrainerWorkloadRequest;
import com.company.gym.entity.*;
import com.company.gym.exception.ValidationException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Date;

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
    @Mock
    private WorkloadClient workloadClient;

    private final MeterRegistry meterRegistry = new SimpleMeterRegistry();

    private TrainingService trainingService;

    private Trainee trainee;
    private Trainer trainer;
    private Date date;

    @BeforeEach
    void setUp() {
        trainingService = new TrainingService(trainingDAO, traineeDAO, trainerDAO, meterRegistry, workloadClient);

        date = new Date();

        User traineeUser = new User();
        traineeUser.setUsername("trainee.user");
        traineeUser.setFirstName("Mari");
        traineeUser.setLastName("Mar");
        traineeUser.setIsActive(true);

        trainee = new Trainee();
        trainee.setUser(traineeUser);
        trainee.setTrainers(new HashSet<>());

        User trainerUser = new User();
        trainerUser.setUsername("trainer.user");
        trainerUser.setFirstName("Lera");
        trainerUser.setLastName("Tes");
        trainerUser.setIsActive(true);

        TrainingType type = new TrainingType();
        type.setName("Zumba");

        trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(type);
    }

    @Test
    void createTraining_ShouldSaveAndCallWorkloadService_WhenValid() {
        when(traineeDAO.findByUsernameWithTrainers("trainee.user")).thenReturn(trainee);
        when(trainerDAO.findByUsername("trainer.user")).thenReturn(trainer);

        trainee.getTrainers().add(trainer);

        Training result = trainingService.createTraining(
                "trainee.user",
                "trainer.user",
                "Super Training",
                date,
                60
        );

        assertNotNull(result);

        verify(trainingDAO, times(1)).save(any(Training.class));

        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(workloadClient, times(1)).updateWorkload(captor.capture());

        TrainerWorkloadRequest sentRequest = captor.getValue();

        assertEquals("trainer.user", sentRequest.getTrainerUsername());
        assertEquals("Lera", sentRequest.getTrainerFirstName());
        assertEquals("Tes", sentRequest.getTrainerLastName());
        assertEquals(true, sentRequest.getIsActive());
        assertEquals(date, sentRequest.getTrainingDate());
        assertEquals(60, sentRequest.getTrainingDuration());
        assertEquals(TrainerWorkloadRequest.ActionType.ADD, sentRequest.getActionType());
    }

    @Test
    void createTraining_ShouldThrowException_WhenTrainerNotAssociated() {
        when(traineeDAO.findByUsernameWithTrainers("trainee.user")).thenReturn(trainee);
        when(trainerDAO.findByUsername("trainer.user")).thenReturn(trainer);

        ValidationException exception = assertThrows(ValidationException.class, () ->
                trainingService.createTraining("trainee.user", "trainer.user", "Name", date, 60)
        );

        assertEquals("Trainer 'trainer.user' is not associated with Trainee 'trainee.user'.", exception.getMessage());

        verify(trainingDAO, never()).save(any());
        verify(workloadClient, never()).updateWorkload(any());
    }

    @Test
    void createTraining_ShouldThrowException_WhenUserNotFound() {
        when(traineeDAO.findByUsernameWithTrainers("unknown")).thenReturn(null);

        assertThrows(ValidationException.class, () ->
                trainingService.createTraining("unknown", "trainer.user", "Name", date, 60)
        );

        verify(workloadClient, never()).updateWorkload(any());
    }
}