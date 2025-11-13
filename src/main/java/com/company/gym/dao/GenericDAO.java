package com.company.gym.dao;

import com.company.gym.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

public abstract class GenericDAO<T, ID extends Serializable> {

    private static final Logger logger = LoggerFactory.getLogger(GenericDAO.class);

    private final Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract ID getEntityId(T entity);

    public T save(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            logger.info("{} saved successfully. ID: {}", entityClass.getSimpleName(), getEntityId(entity));
            return entity;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.error("Transaction rolled back for {} save.", entityClass.getSimpleName(), e);
            }
            throw new RuntimeException("Could not save " + entityClass.getSimpleName(), e);
        }
    }

    public T update(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            var updatedEntity = session.merge(entity);
            transaction.commit();
            logger.info("{} updated successfully. ID: {}", entityClass.getSimpleName(), getEntityId(updatedEntity));
            return updatedEntity;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.error("Transaction rolled back for {} update.", entityClass.getSimpleName(), e);
            }
            throw new RuntimeException("Could not update " + entityClass.getSimpleName(), e);
        }
    }

    public void delete(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(session.contains(entity) ? entity : session.merge(entity));
            transaction.commit();
            logger.info("{} deleted successfully. ID: {}", entityClass.getSimpleName(), getEntityId(entity));
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.error("Transaction rolled back for {} delete.", entityClass.getSimpleName(), e);
            }
            throw new RuntimeException("Could not delete " + entityClass.getSimpleName(), e);
        }
    }

    public T findById(ID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            T entity = session.find(entityClass, id);
            if (entity != null) {
                logger.debug("Found {} with ID: {}", entityClass.getSimpleName(), id);
            } else {
                logger.warn("Could not find {} with ID: {}", entityClass.getSimpleName(), id);
            }
            return entity;
        }
    }

    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM " + entityClass.getSimpleName();
            Query<T> query = session.createQuery(hql, entityClass);
            return query.list();
        }
    }
}