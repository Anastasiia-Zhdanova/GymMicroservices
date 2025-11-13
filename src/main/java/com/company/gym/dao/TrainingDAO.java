package com.company.gym.dao;

import com.company.gym.dao.GenericDAO;
import com.company.gym.entity.Training;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingDAO extends GenericDAO<Training, Long> {

    public TrainingDAO() {
        super(Training.class);
    }

    @Override
    protected Long getEntityId(Training training) {
        return training.getId();
    }
}