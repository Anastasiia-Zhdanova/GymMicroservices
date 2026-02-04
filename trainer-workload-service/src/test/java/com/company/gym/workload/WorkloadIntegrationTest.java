package com.company.gym.workload;

import com.company.gym.workload.dto.TrainerWorkloadRequest;
import com.company.gym.workload.model.TrainerWorkload;
import com.company.gym.workload.repository.TrainerWorkloadRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WorkloadIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrainerWorkloadRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private String validToken;
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        validToken = Jwts.builder()
                .setSubject("integration.user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void shouldUpdateWorkloadEndToEnd() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("integration.trainer");
        request.setTrainerFirstName("Integration");
        request.setTrainerLastName("Test");
        request.setIsActive(true);
        request.setTrainingDate(new Date());
        request.setTrainingDuration(60);
        request.setActionType(TrainerWorkloadRequest.ActionType.ADD);

        mockMvc.perform(post("/api/v1/workload")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Optional<TrainerWorkload> saved = repository.findByUsername("integration.trainer");
        assertTrue(saved.isPresent());
        assertEquals("Integration", saved.get().getFirstName());
        assertEquals(60, saved.get().getYears().get(0).getMonths().get(0).getTotalDuration());
    }

    @Test
    void shouldFailWith403IfNoToken() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        mockMvc.perform(post("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}