package com.company.gym.service;

import com.company.gym.dto.request.TraineeProfileUpdateRequest;
import com.company.gym.dto.request.UpdateTraineeTrainersRequest;
import com.company.gym.dto.response.TraineeProfileResponse;
import com.company.gym.dto.response.TrainerShortResponse;
import com.company.gym.entity.Trainee;
import com.company.gym.entity.Trainer;
import com.company.gym.entity.User;
import com.company.gym.mapper.TraineeMapper;
import com.company.gym.mapper.TrainerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceFacadeTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private TraineeMapper traineeMapper;
    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TraineeServiceFacade facade;

    private Trainee mockTrainee;
    private TraineeProfileResponse mockResponse;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUsername("test.user");
        mockUser.setIsActive(true);

        mockTrainee = new Trainee();
        mockTrainee.setUser(mockUser);
        mockTrainee.setTrainers(Collections.emptySet());

        mockResponse = new TraineeProfileResponse();
        mockResponse.setUsername("test.user");
    }

    @Test
    void getProfile_CallsServiceAndMapper() {
        when(traineeService.selectProfile(anyString())).thenReturn(mockTrainee);
        when(traineeMapper.toTraineeProfileResponse(any(Trainee.class))).thenReturn(mockResponse);

        TraineeProfileResponse result = facade.getProfile("test.user");

        assertEquals("test.user", result.getUsername());
        verify(traineeService, times(1)).selectProfile("test.user");
        verify(traineeMapper, times(1)).toTraineeProfileResponse(mockTrainee);
    }

    @Test
    void updateProfile_CallsServiceAndMapper_UpdatesStatus() {
        TraineeProfileUpdateRequest request = new TraineeProfileUpdateRequest();
        request.setIsActive(false);
        request.setFirstName("New");
        request.setLastName("Name");

        when(traineeService.updateProfile(
                anyString(),
                anyString(),
                anyString(),
                isNull(Date.class),
                isNull(String.class)))
                .thenReturn(mockTrainee);

        when(traineeMapper.toTraineeProfileResponse(any(Trainee.class))).thenReturn(mockResponse);

        facade.updateProfile("test.user", request);

        verify(traineeService, times(1)).updateProfile(
                eq("test.user"), eq("New"), eq("Name"), isNull(Date.class), isNull(String.class));
        verify(traineeService, times(1)).activateDeactivateProfile("test.user", false);

        verify(traineeMapper, times(1)).toTraineeProfileResponse(mockTrainee);

        assertEquals(false, mockUser.getIsActive());
    }

    @Test
    void deleteProfile_CallsServiceAndReturnsNoContent() {
        ResponseEntity<Void> response = facade.deleteProfile("test.user");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(traineeService, times(1)).deleteProfile("test.user");
    }

    @Test
    void updateTrainers_CallsServiceAndMapper() {
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTrainerUsernames(Set.of("trainer.one"));

        Trainer mockTrainer = new Trainer();
        TrainerShortResponse mockShortResponse = new TrainerShortResponse();

        mockTrainee.setTrainers(Set.of(mockTrainer));

        when(traineeService.updateTrainersList(anyString(), any(Set.class))).thenReturn(mockTrainee);
        when(trainerMapper.toTrainerShortResponse(any(Trainer.class))).thenReturn(mockShortResponse);

        List<TrainerShortResponse> result = facade.updateTrainers("test.user", request);

        assertEquals(1, result.size());
        verify(traineeService, times(1)).updateTrainersList("test.user", Set.of("trainer.one"));
        verify(trainerMapper, times(1)).toTrainerShortResponse(mockTrainer);
    }
}