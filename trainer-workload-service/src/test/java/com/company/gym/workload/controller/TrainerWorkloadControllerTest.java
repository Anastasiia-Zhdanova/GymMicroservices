package com.company.gym.workload.controller;

import com.company.gym.workload.dto.TrainerWorkloadRequest;
import com.company.gym.workload.service.TrainerWorkloadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainerWorkloadController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainerWorkloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainerWorkloadService service;

    @Test
    void shouldReturnOkWhenValidRequest() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("trainer.test");
        request.setTrainingDuration(100);
        request.setActionType(TrainerWorkloadRequest.ActionType.ADD);
        request.setTrainingDate(new Date());

        mockMvc.perform(post("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}