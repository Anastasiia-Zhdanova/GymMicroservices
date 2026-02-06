package com.company.gym.client;

import com.company.gym.dto.request.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// name = name of service in Eureka (in application.yml )
@FeignClient(name = "trainer-workload-service")
public interface WorkloadClient {

    @PostMapping("/api/v1/workload")
    void updateWorkload(@RequestBody TrainerWorkloadRequest request);
}