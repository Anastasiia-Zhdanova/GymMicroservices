package com.company.gym.service;

import com.company.gym.dao.UserDAO;
import com.company.gym.entity.User;
import com.company.gym.exception.ValidationException;
import com.company.gym.util.PasswordUtil;
import com.company.gym.util.UserCredentialGenerator;
import com.company.gym.util.UsernameUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private AuthService authService;

    private User mockUser;
    private final String PLAIN_PASSWORD = "testPassword";
    private final String HASHED_PASSWORD = "hashed";

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setUsername("john.doe");
        mockUser.setPassword(HASHED_PASSWORD);
        mockUser.setIsActive(true);
    }

    @Test
    void assignUniqueUsernameAndPassword_Success() {
        try (MockedStatic<UsernameUtil> mockUsernameUtil = Mockito.mockStatic(UsernameUtil.class);
             MockedStatic<UserCredentialGenerator> mockGenerator = Mockito.mockStatic(UserCredentialGenerator.class);
             MockedStatic<PasswordUtil> mockPasswordUtil = Mockito.mockStatic(PasswordUtil.class)) {

            mockUsernameUtil.when(() -> UsernameUtil.generateBaseUsername("John", "Doe")).thenReturn("john.doe");
            when(userDAO.findByUsername("john.doe")).thenReturn(null);
            mockGenerator.when(UserCredentialGenerator::generatePassword).thenReturn(PLAIN_PASSWORD);
            mockPasswordUtil.when(() -> PasswordUtil.hashPassword(PLAIN_PASSWORD)).thenReturn(HASHED_PASSWORD);

            String resultPassword = authService.assignUniqueUsernameAndPassword(mockUser);

            assertEquals(PLAIN_PASSWORD, resultPassword);
            assertEquals("john.doe", mockUser.getUsername());
            assertEquals(HASHED_PASSWORD, mockUser.getPassword());
            assertTrue(mockUser.getIsActive());
        }
    }

    @Test
    void assignUniqueUsernameAndPassword_CollisionResolution() {
        User existingUser = new User();
        try (MockedStatic<UsernameUtil> mockUsernameUtil = Mockito.mockStatic(UsernameUtil.class);
             MockedStatic<UserCredentialGenerator> mockGenerator = Mockito.mockStatic(UserCredentialGenerator.class);
             MockedStatic<PasswordUtil> mockPasswordUtil = Mockito.mockStatic(PasswordUtil.class)) {

            mockUsernameUtil.when(() -> UsernameUtil.generateBaseUsername("John", "Doe")).thenReturn("john.doe");
            when(userDAO.findByUsername("john.doe")).thenReturn(existingUser);
            when(userDAO.findByUsername("john.doe1")).thenReturn(existingUser);
            when(userDAO.findByUsername("john.doe2")).thenReturn(null);

            mockGenerator.when(UserCredentialGenerator::generatePassword).thenReturn(PLAIN_PASSWORD);
            mockPasswordUtil.when(() -> PasswordUtil.hashPassword(PLAIN_PASSWORD)).thenReturn(HASHED_PASSWORD);

            authService.assignUniqueUsernameAndPassword(mockUser);

            assertEquals("john.doe2", mockUser.getUsername());
            verify(userDAO, times(3)).findByUsername(anyString());
        }
    }

    @Test
    void assignUniqueUsernameAndPassword_UsernameUtilException() {
        try (MockedStatic<UsernameUtil> mockUsernameUtil = Mockito.mockStatic(UsernameUtil.class)) {

            mockUsernameUtil.when(() -> UsernameUtil.generateBaseUsername(anyString(), anyString())).thenThrow(new IllegalArgumentException("Error"));

            authService.assignUniqueUsernameAndPassword(mockUser);

            assertEquals("", mockUser.getUsername());
        }
    }

    @Test
    void isUsernameTaken_True() {
        when(userDAO.findByUsername("exists")).thenReturn(mockUser);
        assertTrue(authService.isUsernameTaken("exists"));
    }

    @Test
    void isUsernameTaken_False() {
        when(userDAO.findByUsername("new")).thenReturn(null);
        assertFalse(authService.isUsernameTaken("new"));
    }

    @Test
    void authenticateUser_Success() {
        try (MockedStatic<PasswordUtil> mockPasswordUtil = Mockito.mockStatic(PasswordUtil.class)) {
            when(userDAO.findByUsername("john.doe")).thenReturn(mockUser);
            mockPasswordUtil.when(() -> PasswordUtil.checkPassword(PLAIN_PASSWORD, HASHED_PASSWORD)).thenReturn(true);

            assertTrue(authService.authenticateUser("john.doe", PLAIN_PASSWORD));
        }
    }

    @Test
    void authenticateUser_UserNotFound_Failure() {
        when(userDAO.findByUsername("notfound")).thenReturn(null);

        assertFalse(authService.authenticateUser("notfound", PLAIN_PASSWORD));
    }

    @Test
    void authenticateUser_IncorrectPassword_Failure() {
        try (MockedStatic<PasswordUtil> mockPasswordUtil = Mockito.mockStatic(PasswordUtil.class)) {
            when(userDAO.findByUsername("john.doe")).thenReturn(mockUser);
            mockPasswordUtil.when(() -> PasswordUtil.checkPassword(PLAIN_PASSWORD, HASHED_PASSWORD)).thenReturn(false);

            assertFalse(authService.authenticateUser("john.doe", PLAIN_PASSWORD));
        }
    }

    @Test
    void authenticateUser_DeactivatedUser_Failure() {
        mockUser.setIsActive(false);
        when(userDAO.findByUsername("john.doe")).thenReturn(mockUser);

        assertFalse(authService.authenticateUser("john.doe", PLAIN_PASSWORD));
    }

    @Test
    void changePassword_Success() {
        try (MockedStatic<PasswordUtil> mockPasswordUtil = Mockito.mockStatic(PasswordUtil.class)) {
            when(userDAO.findByUsername("john.doe")).thenReturn(mockUser);
            mockPasswordUtil.when(() -> PasswordUtil.checkPassword("oldPass", HASHED_PASSWORD)).thenReturn(true);
            mockPasswordUtil.when(() -> PasswordUtil.hashPassword("newPass")).thenReturn("newHashed");

            authService.changePassword("john.doe", "oldPass", "newPass");

            assertEquals("newHashed", mockUser.getPassword());
            verify(userDAO).update(mockUser);
        }
    }

    @Test
    void changePassword_UserNotFound_ThrowsException() {
        when(userDAO.findByUsername("notfound")).thenReturn(null);

        assertThrows(ValidationException.class, () -> authService.changePassword("notfound", "oldPass", "newPass"));
        verify(userDAO, never()).update(any(User.class));
    }

    @Test
    void changePassword_IncorrectOldPassword_ThrowsException() {
        try (MockedStatic<PasswordUtil> mockPasswordUtil = Mockito.mockStatic(PasswordUtil.class)) {
            when(userDAO.findByUsername("john.doe")).thenReturn(mockUser);
            mockPasswordUtil.when(() -> PasswordUtil.checkPassword("wrongOldPass", HASHED_PASSWORD)).thenReturn(false);

            assertThrows(ValidationException.class, () -> authService.changePassword("john.doe", "wrongOldPass", "newPass"));
            verify(userDAO, never()).update(any(User.class));
        }
    }
}