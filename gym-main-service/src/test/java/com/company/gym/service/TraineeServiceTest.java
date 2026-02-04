package com.company.gym.service;

import com.company.gym.dao.TraineeDAO;
import com.company.gym.dao.TrainerDAO;
import com.company.gym.dao.UserDAO;
import com.company.gym.entity.Trainee;
import com.company.gym.entity.Trainer;
import com.company.gym.entity.User;
import com.company.gym.exception.NotFoundException;
import com.company.gym.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceTest {

    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private TrainerDAO trainerDAO;
    @Mock
    private UserDAO userDAO;
    @Mock
    private AuthService authService;

    @InjectMocks
    private TraineeService traineeService;

    private Trainee mockTrainee;
    private User mockUser;
    private Trainer mockTrainer1;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUsername("trainee.user");
        mockUser.setFirstName("Trainee");
        mockUser.setLastName("User");
        mockUser.setIsActive(true);
        mockUser.setId(1L);

        mockTrainee = new Trainee();
        mockTrainee.setUser(mockUser);
        mockTrainee.setId(1L);
        mockTrainee.setTrainers(new HashSet<>());

        mockTrainer1 = new Trainer();
        User trainerUser = new User();
        trainerUser.setUsername("trainer.one");
        mockTrainer1.setUser(trainerUser);

        mockTrainee.addTrainer(mockTrainer1);
    }

    @Test
    void createProfile_Success() {
        String plainPassword = "gen_password";
        when(authService.assignUniqueUsernameAndPassword(any(User.class))).thenReturn(plainPassword);
        when(traineeDAO.save(any(Trainee.class))).thenReturn(mockTrainee);

        var response = traineeService.createProfile("New", "Trainee", new Date(), "Address");

        assertNotNull(response);
        assertEquals(mockUser.getUsername(), response.getUsername());
        assertEquals(plainPassword, response.getPassword());
        verify(traineeDAO, times(1)).save(any(Trainee.class));
    }

    @Test
    void createProfile_FailsOnEmptyName() {
        assertThrows(ValidationException.class,
                () -> traineeService.createProfile("", "User", new Date(), "Address"));
    }

    @Test
    void selectProfile_Success() {
        when(traineeDAO.findByUsernameWithTrainers(anyString())).thenReturn(mockTrainee);

        Trainee result = traineeService.selectProfile(mockUser.getUsername());

        assertNotNull(result);
        assertEquals(mockUser.getUsername(), result.getUser().getUsername());
        verify(traineeDAO, times(1)).findByUsernameWithTrainers(mockUser.getUsername());
    }

    @Test
    void selectProfile_NotFound() {
        when(traineeDAO.findByUsernameWithTrainers(anyString())).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> traineeService.selectProfile(mockUser.getUsername()));
    }

    @Test
    void updateProfile_Success() {
        String newFirstName = "NewName";
        when(traineeDAO.findByUsername(anyString())).thenReturn(mockTrainee);
        when(traineeDAO.findByUsernameWithTrainers(anyString())).thenReturn(mockTrainee);

        Trainee result = traineeService.updateProfile(mockUser.getUsername(), newFirstName, "User", new Date(), "New Address");

        assertEquals(newFirstName, mockUser.getFirstName());
        verify(userDAO, times(1)).update(mockUser);
        verify(traineeDAO, times(1)).update(mockTrainee);
    }

    @Test
    void updateProfile_FailsOnNotFound() {
        when(traineeDAO.findByUsername(anyString())).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> traineeService.updateProfile("non.exist", "Name", "Last", new Date(), "Address"));
    }

    @Test
    void deleteProfile_Success() {
        when(traineeDAO.findByUsername(anyString())).thenReturn(mockTrainee);

        traineeService.deleteProfile(mockUser.getUsername());

        verify(traineeDAO, times(1)).delete(mockTrainee);
    }

    @Test
    void activateDeactivateProfile_Success() {
        when(traineeDAO.findByUsername(anyString())).thenReturn(mockTrainee);

        traineeService.activateDeactivateProfile(mockUser.getUsername(), false);

        assertFalse(mockUser.getIsActive());
        verify(userDAO, times(1)).update(mockUser);
    }

    @Test
    void updateTrainersList_Success_FullReplacement() {
        Set<String> newTrainerUsernames = Set.of("trainer.two");
        Trainer mockTrainer2 = new Trainer();
        User trainerUser2 = new User();
        trainerUser2.setUsername("trainer.two");
        mockTrainer2.setUser(trainerUser2);

        when(traineeDAO.findByUsernameWithTrainers(anyString())).thenReturn(mockTrainee);
        when(trainerDAO.findByUserNameWithTrainees("trainer.two")).thenReturn(mockTrainer2);
        when(traineeDAO.update(any(Trainee.class))).thenReturn(mockTrainee);

        Trainee updatedTrainee = traineeService.updateTrainersList(mockUser.getUsername(), newTrainerUsernames);

        assertEquals(1, updatedTrainee.getTrainers().size());
        assertTrue(updatedTrainee.getTrainers().contains(mockTrainer2));
        verify(traineeDAO, times(1)).update(mockTrainee);
    }

    @Test
    void updateTrainersList_IgnoresNonExistingTrainer() {
        Set<String> newTrainerUsernames = Set.of("trainer.two", "non.exist");
        Trainer mockTrainer2 = new Trainer();
        User trainerUser2 = new User();
        trainerUser2.setUsername("trainer.two");
        mockTrainer2.setUser(trainerUser2);

        when(traineeDAO.findByUsernameWithTrainers(anyString())).thenReturn(mockTrainee);
        when(trainerDAO.findByUserNameWithTrainees("trainer.two")).thenReturn(mockTrainer2);
        when(trainerDAO.findByUserNameWithTrainees("non.exist")).thenReturn(null);
        when(traineeDAO.update(any(Trainee.class))).thenReturn(mockTrainee);

        Trainee updatedTrainee = traineeService.updateTrainersList(mockUser.getUsername(), newTrainerUsernames);

        assertEquals(1, updatedTrainee.getTrainers().size());
        verify(traineeDAO, times(1)).update(mockTrainee);
    }
}