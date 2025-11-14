package com.company.gym.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

public abstract class GenericDAO<T, ID extends Serializable> {

    private static final Logger logger = LoggerFactory.getLogger(GenericDAO.class);

    private final Class<T> entityClass;

    @PersistenceContext
    private EntityManager entityManager;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract ID getEntityId(T entity);

    public T save(T entity) {
        try {
            entityManager.persist(entity);
            logger.info("{} saved successfully. ID: {}", entityClass.getSimpleName(), getEntityId(entity));
            return entity;
        } catch (Exception e) {
            logger.error("Could not save {}. Error: {}", entityClass.getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Could not save " + entityClass.getSimpleName(), e);
        }
    }

    public T update(T entity) {
        try {
            T updatedEntity = entityManager.merge(entity);
            logger.info("{} updated successfully. ID: {}", entityClass.getSimpleName(), getEntityId(updatedEntity));
            return updatedEntity;
        } catch (Exception e) {
            logger.error("Could not update {}. Error: {}", entityClass.getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Could not update " + entityClass.getSimpleName(), e);
        }
    }

    public void delete(T entity) {
        try {
            entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
            logger.info("{} deleted successfully. ID: {}", entityClass.getSimpleName(), getEntityId(entity));
        } catch (Exception e) {
            logger.error("Could not delete {}. Error: {}", entityClass.getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Could not delete " + entityClass.getSimpleName(), e);
        }
    }

    public T findById(ID id) {
        T entity = entityManager.find(entityClass, id);
        if (entity != null) {
            logger.debug("Found {} with ID: {}", entityClass.getSimpleName(), id);
        } else {
            logger.warn("Could not find {} with ID: {}", entityClass.getSimpleName(), id);
        }
        return entity;
    }

    public List<T> findAll() {
        String hql = "FROM " + entityClass.getSimpleName();
        TypedQuery<T> query = entityManager.createQuery(hql, entityClass);
        return query.getResultList();
    }
}