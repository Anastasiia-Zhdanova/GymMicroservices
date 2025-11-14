package com.company.gym.dao;

import com.company.gym.entity.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingTypeDAOTest {

    @InjectMocks
    private TrainingTypeDAO trainingTypeDAO;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<TrainingType> typedQuery;

    private TrainingType testTrainingType;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(trainingTypeDAO, "entityManager", entityManager);
        ReflectionTestUtils.setField(trainingTypeDAO, GenericDAO.class, "entityManager", entityManager, EntityManager.class);

        testTrainingType = new TrainingType("Cardio");
        testTrainingType.setId(1L);
    }

    @Test
    void testGetEntityId() {
        assertEquals(1L, trainingTypeDAO.getEntityId(testTrainingType));
    }

    @Test
    void testFindByName_Found() {
        when(entityManager.createQuery(anyString(), eq(TrainingType.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", "Cardio")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(testTrainingType));

        TrainingType foundType = trainingTypeDAO.findByName("Cardio");

        assertNotNull(foundType);
        assertEquals("Cardio", foundType.getName());
    }

    @Test
    void testFindByName_NotFound() {
        when(entityManager.createQuery(anyString(), eq(TrainingType.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", "Unknown")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        TrainingType foundType = trainingTypeDAO.findByName("Unknown");

        assertNull(foundType);
    }

    @Test
    void testFindByName_Exception() {
        when(entityManager.createQuery(anyString(), eq(TrainingType.class))).thenThrow(new RuntimeException("DB error"));

        TrainingType foundType = trainingTypeDAO.findByName("Cardio");

        assertNull(foundType);
    }

    @Test
    void testFindAll_Overridden() {
        when(entityManager.createQuery("FROM TrainingType t ORDER BY t.name", TrainingType.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(testTrainingType));

        List<TrainingType> types = trainingTypeDAO.findAll();

        assertNotNull(types);
        assertEquals(1, types.size());
    }
}