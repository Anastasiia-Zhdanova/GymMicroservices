package com.company.gym.dao;

import com.company.gym.entity.Trainer;
import com.company.gym.entity.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class TrainerDAO extends GenericDAO<Trainer, Long> {

    private static final Logger logger = LoggerFactory.getLogger(TrainerDAO.class);

    @PersistenceContext
    private EntityManager entityManager;

    public TrainerDAO() {
        super(Trainer.class);
    }

    @Override
    protected Long getEntityId(Trainer trainer) {
        return trainer.getId();
    }

    public Trainer findByUsername(String username) {
        try {
            TypedQuery<Trainer> query = entityManager.createQuery(
                    "SELECT t FROM Trainer t JOIN FETCH t.user u WHERE u.username = :username", Trainer.class);
            query.setParameter("username", username);

            List<Trainer> results = query.getResultList();
            Trainer trainer = results.isEmpty() ? null : results.get(0);

            if (trainer == null) {
                logger.warn("Trainer not found with username: {}", username);
            } else {
                logger.debug("Found Trainer with username: {}", username);
            }
            return trainer;
        } catch (Exception e) {
            logger.error("Error finding Trainer by username: {}", username, e);
            return null;
        }
    }

    public Trainer findByUserNameWithTrainees(String username) {
        try {
            TypedQuery<Trainer> query = entityManager.createQuery(
                    "SELECT DISTINCT t FROM Trainer t " +
                            "JOIN FETCH t.user u " +
                            "LEFT JOIN FETCH t.trainees tr " +
                            "LEFT JOIN FETCH tr.user " +
                            "WHERE u.username = :username", Trainer.class);
            query.setParameter("username", username);

            List<Trainer> results = query.getResultList();
            Trainer trainer = results.isEmpty() ? null : results.get(0);

            if (trainer == null) {
                logger.warn("Trainer not found with username: {}", username);
            } else {
                logger.debug("Found Trainer with username: {}", username);
            }
            return trainer;
        } catch (Exception e) {
            logger.error("Error finding Trainer by username with trainees: {}", username, e);
            return null;
        }
    }

    public List<Training> getTrainerTrainingsList(String username, Date fromDate, Date toDate) {
        StringBuilder hql = new StringBuilder(
                "SELECT t FROM Training t JOIN t.trainer tr JOIN FETCH t.trainee tre JOIN tr.user u WHERE u.username = :username"
        );

        if (fromDate != null) {
            hql.append(" AND t.trainingDate >= :fromDate");
        }
        if (toDate != null) {
            hql.append(" AND t.trainingDate <= :toDate");
        }

        TypedQuery<Training> query = entityManager.createQuery(hql.toString(), Training.class);

        query.setParameter("username", username);
        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }

        List<Training> trainings = query.getResultList();
        logger.info("Retrieved {} trainings for trainer: {}", trainings.size(), username);
        return trainings;
    }

    public List<Trainer> findUnassignedTrainers(String traineeUsername) {
        logger.debug("Finding all active Trainers not assigned to Trainee: {}", traineeUsername);

        try {
            String findTraineeIdHQL = "SELECT t.id FROM Trainee t JOIN t.user u WHERE u.username = :username";
            TypedQuery<Long> traineeIdQuery = entityManager.createQuery(findTraineeIdHQL, Long.class);
            traineeIdQuery.setParameter("username", traineeUsername);

            List<Long> idResults = traineeIdQuery.getResultList();
            Long traineeId = idResults.isEmpty() ? null : idResults.get(0);

            if (traineeId == null) {
                logger.warn("Trainee with username {} not found. Returning empty list.", traineeUsername);
                return List.of();
            }

            String hql = "SELECT t FROM Trainer t WHERE t.id NOT IN (" +
                    "    SELECT tr.id FROM Trainee trainee JOIN trainee.trainers tr WHERE trainee.id = :traineeId" +
                    ") AND t.user.isActive = true";

            TypedQuery<Trainer> query = entityManager.createQuery(hql, Trainer.class);
            query.setParameter("traineeId", traineeId);

            List<Trainer> unassignedTrainers = query.getResultList();
            logger.debug("Found {} unassigned Trainers.", unassignedTrainers.size());
            return unassignedTrainers;

        } catch (Exception e) {
            logger.error("Error finding unassigned trainers for Trainee {}.", traineeUsername, e);
            return List.of();
        }
    }
}