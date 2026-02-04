package com.company.gym.workload.repository;

import com.company.gym.workload.model.TrainerWorkload;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkload, String> {
    Optional<TrainerWorkload> findByUsername(String username);
}