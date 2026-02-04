package com.company.gym.service;

import com.company.gym.dao.TrainingTypeDAO;
import com.company.gym.entity.TrainingType;
import com.company.gym.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeDAO trainingTypeDAO;

    @InjectMocks
    private TrainingTypeService trainingTypeService;

    private TrainingType mockType;

    @BeforeEach
    void setUp() {
        mockType = new TrainingType("Yoga");
        mockType.setId(1L);
    }

    @Test
    void getAllTrainingTypes_ReturnsList() {
        List<TrainingType> expectedList = List.of(mockType, new TrainingType("Cardio"));
        when(trainingTypeDAO.findAll()).thenReturn(expectedList);

        List<TrainingType> actualList = trainingTypeService.getAllTrainingTypes();

        assertEquals(2, actualList.size());
        assertTrue(actualList.contains(mockType));
        verify(trainingTypeDAO).findAll();
    }

    @Test
    void getAllTrainingTypes_ReturnsEmptyList() {
        when(trainingTypeDAO.findAll()).thenReturn(Collections.emptyList());

        List<TrainingType> actualList = trainingTypeService.getAllTrainingTypes();

        assertTrue(actualList.isEmpty());
        verify(trainingTypeDAO).findAll();
    }

    @Test
    void findTrainingTypeById_Success() {
        when(trainingTypeDAO.findById(1L)).thenReturn(mockType);

        TrainingType result = trainingTypeService.findTrainingTypeById(1L);

        assertNotNull(result);
        assertEquals("Yoga", result.getName());
        verify(trainingTypeDAO).findById(1L);
    }

    @Test
    void findTrainingTypeById_NotFound_ThrowsException() {
        when(trainingTypeDAO.findById(99L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> trainingTypeService.findTrainingTypeById(99L));
        verify(trainingTypeDAO).findById(99L);
    }
}