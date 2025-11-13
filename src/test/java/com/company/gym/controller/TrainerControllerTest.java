package com.company.gym.controller;

import com.company.gym.config.CustomUsernamePasswordAuthenticationFilter;
import com.company.gym.config.WebSecurityConfig;
import com.company.gym.dto.request.TrainerProfileUpdateRequest;
import com.company.gym.dto.request.UserStatusUpdateRequest;
import com.company.gym.dto.response.TrainerProfileResponse;
import com.company.gym.dto.response.TrainerShortResponse;
import com.company.gym.dto.response.TrainingListResponse;
import com.company.gym.exception.AuthenticationException;
import com.company.gym.service.AuthService;
import com.company.gym.service.TrainerServiceFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerController.class)
@Import({WebSecurityConfig.class, CustomUsernamePasswordAuthenticationFilter.class})
public class TrainerControllerTest {

    private static final String TRAINER_USERNAME = "trainer.user";
    private static final String TRAINEE_USERNAME = "trainee.user";
    private static final String OTHER_USERNAME = "other.user";
    private static final String BASE_URL = "/api/v1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainerServiceFacade trainerServiceFacade;

    @MockBean
    private AuthService authService;

    private UserDetails mockPrincipal;
    private TrainerProfileResponse mockProfileResponse;
    private TrainerProfileUpdateRequest mockUpdateRequest;

    @BeforeEach
    void setUp() {
        mockPrincipal = User.withUsername(TRAINER_USERNAME).password("pass").roles("USER").build();

        mockProfileResponse = new TrainerProfileResponse();
        mockProfileResponse.setUsername(TRAINER_USERNAME);

        mockUpdateRequest = new TrainerProfileUpdateRequest();
        mockUpdateRequest.setFirstName("New");
        mockUpdateRequest.setLastName("Name");
        mockUpdateRequest.setIsActive(true);
        mockUpdateRequest.setSpecializationId(1L);
    }

    @Test
    void profileEndpoint_AccessDeniedWhenUserIsNotOwner() throws Exception {
        mockMvc.perform(get(BASE_URL + "/trainers/{username}", OTHER_USERNAME)
                        .with(user(mockPrincipal)))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AuthenticationException));
    }

    @Test
    void getProfile_Success() throws Exception {
        when(trainerServiceFacade.getProfile(TRAINER_USERNAME)).thenReturn(mockProfileResponse);

        mockMvc.perform(get(BASE_URL + "/trainers/{username}", TRAINER_USERNAME)
                        .with(user(mockPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TRAINER_USERNAME));

        verify(trainerServiceFacade, times(1)).getProfile(TRAINER_USERNAME);
    }

    @Test
    void updateProfile_Success() throws Exception {
        when(trainerServiceFacade.updateProfile(eq(TRAINER_USERNAME), any(TrainerProfileUpdateRequest.class)))
                .thenReturn(mockProfileResponse);

        mockMvc.perform(put(BASE_URL + "/trainers/{username}", TRAINER_USERNAME)
                        .with(user(mockPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUpdateRequest)))
                .andExpect(status().isOk());

        verify(trainerServiceFacade, times(1)).updateProfile(eq(TRAINER_USERNAME), any(TrainerProfileUpdateRequest.class));
    }

    @Test
    void updateProfile_FailsOnValidation() throws Exception {
        TrainerProfileUpdateRequest invalidRequest = new TrainerProfileUpdateRequest();
        invalidRequest.setFirstName(null);
        invalidRequest.setLastName("Last");
        invalidRequest.setIsActive(true);

        mockMvc.perform(put(BASE_URL + "/trainers/{username}", TRAINER_USERNAME)
                        .with(user(mockPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void getUnassignedTrainers_Success() throws Exception {
        UserDetails traineePrincipal = User.withUsername(TRAINEE_USERNAME).password("pass").roles("USER").build();
        TrainerShortResponse mockTrainerShort = new TrainerShortResponse();
        mockTrainerShort.setUsername("unassigned.trainer");

        when(trainerServiceFacade.getUnassignedTrainers(TRAINEE_USERNAME)).thenReturn(List.of(mockTrainerShort));

        mockMvc.perform(get(BASE_URL + "/trainees/{traineeUsername}/unassigned-trainers", TRAINEE_USERNAME)
                        .with(user(traineePrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("unassigned.trainer"));

        verify(trainerServiceFacade, times(1)).getUnassignedTrainers(TRAINEE_USERNAME);
    }

    @Test
    void getUnassignedTrainers_AccessDeniedWhenNotTraineeOwner() throws Exception {
        mockMvc.perform(get(BASE_URL + "/trainees/{traineeUsername}/unassigned-trainers", TRAINEE_USERNAME)
                        .with(user(mockPrincipal)))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AuthenticationException));
    }

    @Test
    void getTrainings_Success_WithFilters() throws Exception {
        TrainingListResponse mockTrainingResponse = new TrainingListResponse();
        when(trainerServiceFacade.getTrainings(eq(TRAINER_USERNAME), any(Date.class), any(Date.class)))
                .thenReturn(List.of(mockTrainingResponse));

        mockMvc.perform(get(BASE_URL + "/trainers/{username}/trainings", TRAINER_USERNAME)
                        .with(user(mockPrincipal))
                        .param("fromDate", "2023-01-01")
                        .param("toDate", "2023-12-31"))
                .andExpect(status().isOk());

        verify(trainerServiceFacade, times(1)).getTrainings(eq(TRAINER_USERNAME), any(Date.class), any(Date.class));
    }

    @Test
    void getTrainings_Success_WithoutFilters() throws Exception {
        when(trainerServiceFacade.getTrainings(eq(TRAINER_USERNAME), isNull(), isNull()))
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/trainers/{username}/trainings", TRAINER_USERNAME)
                        .with(user(mockPrincipal)))
                .andExpect(status().isOk());

        verify(trainerServiceFacade, times(1)).getTrainings(eq(TRAINER_USERNAME), isNull(), isNull());
    }

    @Test
    void updateStatus_Success() throws Exception {
        UserStatusUpdateRequest request = new UserStatusUpdateRequest();
        request.setIsActive(false);

        mockMvc.perform(patch(BASE_URL + "/trainers/{username}/status", TRAINER_USERNAME)
                        .with(user(mockPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(trainerServiceFacade, times(1)).updateStatus(eq(TRAINER_USERNAME), any(UserStatusUpdateRequest.class));
    }

    @Test
    void updateStatus_FailsOnValidation() throws Exception {
        UserStatusUpdateRequest invalidRequest = new UserStatusUpdateRequest();
        invalidRequest.setIsActive(null);

        mockMvc.perform(patch(BASE_URL + "/trainers/{username}/status", TRAINER_USERNAME)
                        .with(user(mockPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}