package com.company.gym.dao;

import com.company.gym.entity.Trainee;
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
public class TraineeDAO extends GenericDAO<Trainee, Long> {

    private static final Logger logger = LoggerFactory.getLogger(TraineeDAO.class);

    public TraineeDAO() {
        super(Trainee.class);
    }

    @Override
    protected Long getEntityId(Trainee trainee) {
        return trainee.getId();
    }

    public Trainee findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Trainee> query = session.createQuery(
                    "SELECT t FROM Trainee t JOIN FETCH t.user u WHERE u.username = :username", Trainee.class);
            query.setParameter("username", username);
            Trainee trainee = query.uniqueResult();

            if (trainee == null) {
                logger.warn("Trainee not found with username: {}", username);
            } else {
                logger.debug("Found Trainee with username: {}", username);
            }
            return trainee;
        }
    }

    public Trainee findByUsernameWithTrainers(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Trainee> query = session.createQuery(
                    "SELECT DISTINCT t FROM Trainee t " +
                    "JOIN FETCH t.user u " +
                    "LEFT JOIN FETCH t.trainers tr " +
                    "LEFT JOIN FETCH tr.user " +
                    "WHERE u.username = :username", Trainee.class);
            query.setParameter("username", username);
            Trainee trainee = query.uniqueResult();

            if (trainee == null) {
                logger.warn("Trainee not found with username: {}", username);
            } else {
                logger.debug("Found Trainee with username: {}", username);
            }
            return trainee;
        }
    }


    @Override
    public void delete(Trainee trainee) {
        super.delete(trainee);
        logger.info("Trainee profile and associated trainings cascade deleted. ID: {}", trainee.getId());
    }

    public List<Training> getTraineeTrainingsList(String username, Date fromDate, Date toDate, String trainerName, String trainingTypeName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder(
                    "SELECT t FROM Training t JOIN t.trainee tr JOIN FETCH t.trainer tn JOIN t.trainingType tt WHERE tr.user.username = :username"
            );

            if (fromDate != null) hql.append(" AND t.trainingDate >= :fromDate");
            if (toDate != null) hql.append(" AND t.trainingDate <= :toDate");

            if (trainerName != null && !trainerName.isEmpty()) {
                hql.append(" AND CONCAT(tn.user.firstName, ' ', tn.user.lastName) LIKE :trainerName");
            }
            if (trainingTypeName != null && !trainingTypeName.isEmpty()) hql.append(" AND tt.name = :trainingTypeName");

            Query<Training> query = QueryUtil.getTrainingQuery(username,fromDate,toDate,session,hql);

            if (trainerName != null && !trainerName.isEmpty()) query.setParameter("trainerName", "%" + trainerName + "%");
            if (trainingTypeName != null && !trainingTypeName.isEmpty()) query.setParameter("trainingTypeName", trainingTypeName);

            List<Training> trainings = query.list();
            logger.info("Retrieved {} trainings for trainee: {}", trainings.size(), username);
            return trainings;
        }
    }
}