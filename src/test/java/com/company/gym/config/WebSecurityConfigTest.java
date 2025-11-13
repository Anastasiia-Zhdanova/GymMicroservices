package com.company.gym.config;

import com.company.gym.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {})
@Import({WebSecurityConfig.class, CustomUsernamePasswordAuthenticationFilter.class})
public class WebSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private TraineeService traineeService;
    @MockBean
    private TrainerService trainerService;
    @MockBean
    private TraineeServiceFacade traineeServiceFacade;
    @MockBean
    private TrainerServiceFacade trainerServiceFacade;
    @MockBean
    private TrainingService trainingService;

    @Test
    void publicEndpoints_ShouldBePermitted() throws Exception {
        mockMvc.perform(post("/api/v1/auth/trainee/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/auth/trainer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginEndpoint_ShouldBeAnonymous() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("test.user")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_ShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/trainees/test"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/trainees/test.user")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("test.user")))
                .andExpect(status().isOk());
    }

    @Test
    void logoutEndpoint_ShouldReturnOkAndBePermitted() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk());
    }

    @Test
    void userDetailsService_FailsWhenUserNotFound() {
        when(authService.isUsernameTaken(anyString())).thenReturn(false);

        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> new WebSecurityConfig(authService).userDetailsService().loadUserByUsername("non.exist"));
    }

    @Test
    void userDetailsService_Success() {
        when(authService.isUsernameTaken(anyString())).thenReturn(true);

        UserDetails userDetails = new WebSecurityConfig(authService).userDetailsService().loadUserByUsername("exist");

        assertEquals("exist", userDetails.getUsername());
    }
}