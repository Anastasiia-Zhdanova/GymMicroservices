package com.company.gym.dao;

import com.company.gym.entity.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrainingTypeDAO extends GenericDAO<TrainingType, Long> {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTypeDAO.class);

    @PersistenceContext
    private EntityManager entityManager;

    public TrainingTypeDAO() {
        super(TrainingType.class);
    }

    @Override
    protected Long getEntityId(TrainingType trainingType) {
        return trainingType.getId();
    }

    public TrainingType findByName(String name) {
        try {
            TypedQuery<TrainingType> query = entityManager.createQuery(
                    "SELECT t FROM TrainingType t WHERE t.name = :name", TrainingType.class);
            query.setParameter("name", name);

            List<TrainingType> results = query.getResultList();
            TrainingType trainingType = results.isEmpty() ? null : results.get(0);

            if (trainingType == null) {
                logger.warn("TrainingType not found with name: {}", name);
            } else {
                logger.debug("Found TrainingType with name: {}", name);
            }
            return trainingType;

        } catch (Exception e) {
            logger.error("Error finding TrainingType by name: {}", name, e);
            return null;
        }
    }

    @Override
    public List<TrainingType> findAll() {
        TypedQuery<TrainingType> query = entityManager.createQuery(
                "FROM TrainingType t ORDER BY t.name", TrainingType.class);

        List<TrainingType> types = query.getResultList();
        logger.debug("Found {} Training Types.", types.size());
        return types;
    }
}