package com.company.gym.dao;

import com.company.gym.entity.TrainingType;
import com.company.gym.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrainingTypeDAO extends GenericDAO<TrainingType, Long> {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTypeDAO.class);

    public TrainingTypeDAO() {
        super(TrainingType.class);
    }

    @Override
    protected Long getEntityId(TrainingType trainingType) {
        return trainingType.getId();
    }

    public TrainingType findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<TrainingType> query = session.createQuery(
                    "SELECT t FROM TrainingType t WHERE t.name = :name", TrainingType.class);
            query.setParameter("name", name);
            TrainingType trainingType = query.uniqueResult();

            if (trainingType == null) {
                logger.warn("TrainingType not found with name: {}", name);
            } else {
                logger.debug("Found TrainingType with name: {}", name);
            }
            return trainingType;
        }
    }

    @Override
    public List<TrainingType> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<TrainingType> query = session.createQuery(
                    "FROM TrainingType t ORDER BY t.name", TrainingType.class);

            List<TrainingType> types = query.getResultList();
            logger.debug("Found {} Training Types.", types.size());
            return types;
        }
    }
}