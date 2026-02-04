package com.company.gym.workload.controller;

import com.company.gym.workload.dto.TrainerWorkloadRequest;
import com.company.gym.workload.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workload")
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadController {

    private final TrainerWorkloadService service;

    @PostMapping
    public ResponseEntity<Void> updateWorkload(@RequestBody TrainerWorkloadRequest request) {
        log.info("Received workload update request for {}", request.getTrainerUsername());
        service.updateWorkload(request);
        return ResponseEntity.ok().build();
    }
}