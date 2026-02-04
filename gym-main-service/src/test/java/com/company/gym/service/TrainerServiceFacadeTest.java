package com.company.gym.service;

import com.company.gym.dto.request.TrainerProfileUpdateRequest;
import com.company.gym.dto.request.UserStatusUpdateRequest;
import com.company.gym.dto.response.TrainerProfileResponse;
import com.company.gym.dto.response.TrainerShortResponse;
import com.company.gym.dto.response.TrainingListResponse;
import com.company.gym.dto.response.TrainingTypeResponse;
import com.company.gym.entity.Trainer;
import com.company.gym.entity.Training;
import com.company.gym.entity.TrainingType;
import com.company.gym.entity.User;
import com.company.gym.mapper.TrainerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceFacadeTest {

    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainerServiceFacade trainerServiceFacade;

    private final String USERNAME = "trainer.user";
    private Trainer mockTrainer;
    private TrainerProfileResponse mockResponse;
    private TrainerProfileUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        mockTrainer = new Trainer();
        mockTrainer.setUser(new User());
        mockResponse = new TrainerProfileResponse();

        updateRequest = new TrainerProfileUpdateRequest();
        updateRequest.setFirstName("New");
        updateRequest.setLastName("Name");
        updateRequest.setSpecializationId(1L);
        updateRequest.setIsActive(true);
    }

    @Test
    void getProfile_Success() {
        when(trainerService.selectProfile(USERNAME)).thenReturn(mockTrainer);
        when(trainerMapper.toTrainerProfileResponse(mockTrainer)).thenReturn(mockResponse);

        TrainerProfileResponse result = trainerServiceFacade.getProfile(USERNAME);

        assertEquals(mockResponse, result);
        verify(trainerService).selectProfile(USERNAME);
        verify(trainerMapper).toTrainerProfileResponse(mockTrainer);
    }

    @Test
    void updateProfile_Success() {
        when(trainerService.updateProfile(
                eq(USERNAME), anyString(), anyString(), anyLong()
        )).thenReturn(mockTrainer);
        when(trainerMapper.toTrainerProfileResponse(mockTrainer)).thenReturn(mockResponse);

        TrainerProfileResponse result = trainerServiceFacade.updateProfile(USERNAME, updateRequest);

        assertEquals(mockResponse, result);
        verify(trainerService).updateProfile(USERNAME, "New", "Name", 1L);
        verify(trainerService).activateDeactivateProfile(USERNAME, true);
        verify(trainerMapper).toTrainerProfileResponse(mockTrainer);
        assertTrue(mockTrainer.getUser().getIsActive());
    }

    @Test
    void getUnassignedTrainers_Success() {
        Trainer mockTrainerEntity = new Trainer();
        List<Trainer> mockTrainers = List.of(mockTrainerEntity);
        List<TrainerShortResponse> mockResponses = List.of(new TrainerShortResponse());

        when(trainerService.getUnassignedTrainers("trainee.user")).thenReturn(mockTrainers);
        when(trainerMapper.toTrainerShortResponseList(mockTrainers)).thenReturn(mockResponses);

        List<TrainerShortResponse> result = trainerServiceFacade.getUnassignedTrainers("trainee.user");

        assertEquals(mockResponses, result);
        verify(trainerService).getUnassignedTrainers("trainee.user");
        verify(trainerMapper).toTrainerShortResponseList(mockTrainers);
    }

    @Test
    void getTrainings_Success() {
        Date from = new Date();
        Date to = new Date();
        Training mockTraining = new Training();
        List<Training> mockTrainings = List.of(mockTraining);
        List<TrainingListResponse> mockTrainingResponses = List.of(new TrainingListResponse());

        when(trainerService.getTrainerTrainingsList(USERNAME, from, to)).thenReturn(mockTrainings);
        when(trainerMapper.toTrainingListResponse(mockTrainings)).thenReturn(mockTrainingResponses);

        List<TrainingListResponse> result = trainerServiceFacade.getTrainings(USERNAME, from, to);

        assertEquals(mockTrainingResponses, result);
        verify(trainerService).getTrainerTrainingsList(USERNAME, from, to);
        verify(trainerMapper).toTrainingListResponse(mockTrainings);
    }

    @Test
    void updateStatus_Success() {
        UserStatusUpdateRequest request = new UserStatusUpdateRequest();
        request.setIsActive(false);

        trainerServiceFacade.updateStatus(USERNAME, request);

        verify(trainerService).activateDeactivateProfile(USERNAME, false);
    }

    @Test
    void getAllTrainingTypes_Success() {
        TrainingType mockType = new TrainingType();
        List<TrainingType> mockTypes = List.of(mockType);
        List<TrainingTypeResponse> mockResponses = List.of(new TrainingTypeResponse());

        when(trainingTypeService.getAllTrainingTypes()).thenReturn(mockTypes);
        when(trainerMapper.toTrainingTypeResponseList(mockTypes)).thenReturn(mockResponses);

        List<TrainingTypeResponse> result = trainerServiceFacade.getAllTrainingTypes();

        assertEquals(mockResponses, result);
        verify(trainingTypeService).getAllTrainingTypes();
        verify(trainerMapper).toTrainingTypeResponseList(mockTypes);
    }
}