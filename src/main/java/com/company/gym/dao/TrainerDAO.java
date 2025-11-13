package com.company.gym.dao;

import com.company.gym.entity.Trainer;
import com.company.gym.entity.Training;
import com.company.gym.util.HibernateUtil;
import com.company.gym.util.QueryUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class TrainerDAO extends GenericDAO<Trainer, Long> {

    private static final Logger logger = LoggerFactory.getLogger(TrainerDAO.class);

    public TrainerDAO() {
        super(Trainer.class);
    }

    @Override
    protected Long getEntityId(Trainer trainer) {
        return trainer.getId();
    }

    public Trainer findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Trainer> query = session.createQuery(
                    "SELECT t FROM Trainer t JOIN FETCH t.user u WHERE u.username = :username", Trainer.class);
            query.setParameter("username", username);
            Trainer trainer = query.uniqueResult();

            if (trainer == null) {
                logger.warn("Trainer not found with username: {}", username);
            } else {
                logger.debug("Found Trainer with username: {}", username);
            }
            return trainer;
        }
    }

    public Trainer findByUserNameWithTrainees(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Trainer> query = session.createQuery(
                    "SELECT DISTINCT t FROM Trainer t " +
                    "JOIN FETCH t.user u " +
                    "LEFT JOIN FETCH t.trainees tr " +
                    "LEFT JOIN FETCH tr.user " +
                    "WHERE u.username = :username", Trainer.class);
            query.setParameter("username", username);
            Trainer trainer = query.uniqueResult();

            if (trainer == null) {
                logger.warn("Trainer not found with username: {}", username);
            } else {
                logger.debug("Found Trainer with username: {}", username);
            }
            return trainer;
        }
    }

    public List<Training> getTrainerTrainingsList(String username, Date fromDate, Date toDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder(
                    "SELECT t FROM Training t JOIN t.trainer tr JOIN FETCH t.trainee tre JOIN tr.user u WHERE u.username = :username"
            );

            if (fromDate != null) {
                hql.append(" AND t.trainingDate >= :fromDate");
            }
            if (toDate != null) {
                hql.append(" AND t.trainingDate <= :toDate");
            }

            Query<Training> query = QueryUtil.getTrainingQuery(username,fromDate,toDate,session,hql);

            List<Training> trainings = query.getResultList();
            logger.info("Retrieved {} trainings for trainer: {}", trainings.size(), username);
            return trainings;
        }
    }

    public List<Trainer> findUnassignedTrainers(String traineeUsername) {
        logger.debug("Finding all active Trainers not assigned to Trainee: {}", traineeUsername);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String findTraineeIdHQL = "SELECT t.id FROM Trainee t JOIN t.user u WHERE u.username = :username";
            Query<Long> traineeIdQuery = session.createQuery(findTraineeIdHQL, Long.class);
            traineeIdQuery.setParameter("username", traineeUsername);
            Long traineeId = traineeIdQuery.uniqueResult();

            if (traineeId == null) {
                logger.warn("Trainee with username {} not found. Returning empty list.", traineeUsername);
                return List.of();
            }

            String hql = "SELECT t FROM Trainer t WHERE t.id NOT IN (" +
                    "    SELECT tr.id FROM Trainee trainee JOIN trainee.trainers tr WHERE trainee.id = :traineeId" +
                    ") AND t.user.isActive = true";

            Query<Trainer> query = session.createQuery(hql, Trainer.class);
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