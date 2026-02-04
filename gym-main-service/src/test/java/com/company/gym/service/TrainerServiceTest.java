package com.company.gym.service;

import com.company.gym.dao.TrainerDAO;
import com.company.gym.dao.TrainingTypeDAO;
import com.company.gym.dao.UserDAO;
import com.company.gym.entity.Trainer;
import com.company.gym.entity.TrainingType;
import com.company.gym.entity.User;
import com.company.gym.exception.NotFoundException;
import com.company.gym.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {

    @Mock
    private TrainerDAO trainerDAO;
    @Mock
    private UserDAO userDAO;
    @Mock
    private TrainingTypeDAO trainingTypeDAO;
    @Mock
    private AuthService authService;

    @InjectMocks
    private TrainerService trainerService;

    private Trainer mockTrainer;
    private User mockUser;
    private TrainingType mockSpecialization;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUsername("trainer.user");
        mockUser.setFirstName("Trainer");
        mockUser.setLastName("User");
        mockUser.setIsActive(true);

        mockSpecialization = new TrainingType("Yoga");
        mockSpecialization.setId(1L);

        mockTrainer = new Trainer();
        mockTrainer.setUser(mockUser);
        mockTrainer.setSpecialization(mockSpecialization);
    }

    @Test
    void createProfile_Success() {
        String plainPassword = "gen_password";
        when(trainingTypeDAO.findById(anyLong())).thenReturn(mockSpecialization);
        when(authService.assignUniqueUsernameAndPassword(any(User.class))).thenReturn(plainPassword);
        when(trainerDAO.save(any(Trainer.class))).thenReturn(mockTrainer);

        var response = trainerService.createProfile("New", "Trainer", 1L);

        assertNotNull(response);
        assertEquals(mockUser.getUsername(), response.getUsername());
        assertEquals(plainPassword, response.getPassword());
        verify(trainerDAO, times(1)).save(any(Trainer.class));
    }

    @Test
    void createProfile_FailsOnMissingSpecialization() {
        when(trainingTypeDAO.findById(anyLong())).thenReturn(null);

        assertThrows(ValidationException.class,
                () -> trainerService.createProfile("New", "Trainer", 99L));
    }

    @Test
    void selectProfile_Success() {
        when(trainerDAO.findByUserNameWithTrainees(anyString())).thenReturn(mockTrainer);

        Trainer result = trainerService.selectProfile(mockUser.getUsername());

        assertNotNull(result);
        assertEquals(mockUser.getUsername(), result.getUser().getUsername());
    }

    @Test
    void selectProfile_NotFound() {
        when(trainerDAO.findByUserNameWithTrainees(anyString())).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> trainerService.selectProfile(mockUser.getUsername()));
    }

    @Test
    void updateProfile_Success_SpecializationIgnored() {
        String newFirstName = "UpdatedName";
        Long oldSpecializationId = mockSpecialization.getId();

        when(trainerDAO.findByUsername(anyString())).thenReturn(mockTrainer);
        when(trainerDAO.findByUserNameWithTrainees(anyString())).thenReturn(mockTrainer);

        Trainer result = trainerService.updateProfile(mockUser.getUsername(), newFirstName, "User", 2L);

        assertEquals(newFirstName, mockUser.getFirstName());
        assertEquals(oldSpecializationId, mockTrainer.getSpecialization().getId());
        verify(userDAO, times(1)).update(mockUser);
    }

    @Test
    void activateDeactivateProfile_Success() {
        when(trainerDAO.findByUsername(anyString())).thenReturn(mockTrainer);

        trainerService.activateDeactivateProfile(mockUser.getUsername(), false);

        assertFalse(mockUser.getIsActive());
        verify(userDAO, times(1)).update(mockUser);
    }

    @Test
    void getUnassignedTrainers_Success() {
        List<Trainer> unassignedList = List.of(mockTrainer);
        when(trainerDAO.findUnassignedTrainers(anyString())).thenReturn(unassignedList);

        List<Trainer> result = trainerService.getUnassignedTrainers("trainee.user");

        assertFalse(result.isEmpty());
        verify(trainerDAO, times(1)).findUnassignedTrainers("trainee.user");
    }
}