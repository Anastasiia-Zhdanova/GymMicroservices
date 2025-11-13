package com.company.gym.service;

import com.company.gym.dao.UserDAO;
import com.company.gym.entity.User;
import com.company.gym.exception.ValidationException;
import com.company.gym.util.PasswordUtil;
import com.company.gym.util.UserCredentialGenerator;
import com.company.gym.util.UsernameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Transactional
    public <T extends User> String assignUniqueUsernameAndPassword(T user) {
        logger.info("Generating unique username and password for user: {} {}", user.getFirstName(), user.getLastName());

        String baseUsername = generateUserName(user.getFirstName(), user.getLastName());
        String uniqueUsername = baseUsername;
        int counter = 0;

        while (isUsernameTaken(uniqueUsername)) {
            counter++;
            uniqueUsername = baseUsername + counter;
        }

        user.setUsername(uniqueUsername);
        var plainPassword = UserCredentialGenerator.generatePassword();
        user.setPassword(PasswordUtil.hashPassword(plainPassword));
        user.setIsActive(true);

        logger.info("Assigned username: {}", user.getUsername());
        return plainPassword;
    }

    public boolean isUsernameTaken(String username) {
        return userDAO.findByUsername(username) != null;
    }

    private String generateUserName(String firstName, String lastName) {
        try {
            return UsernameUtil.generateBaseUsername(firstName, lastName);
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    public boolean authenticateUser(String username, String password) {
        User user = userDAO.findByUsername(username);

        if (user == null) {
            logger.warn("Authentication failed: User '{}' not found.", username);
            return false;
        }

        if (!user.getIsActive()) {
            logger.warn("Authentication failed: User '{}' is deactivated.", username);
            return false;
        }

        boolean isAuthenticated = PasswordUtil.checkPassword(password, user.getPassword());

        if (isAuthenticated) {
            logger.info("User '{}' authenticated successfully.", username);
        } else {
            logger.warn("Authentication failed: Invalid password for User '{}'.", username);
        }
        return isAuthenticated;
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new ValidationException("User profile not found for password change: " + username);
        }

        if (!PasswordUtil.checkPassword(oldPassword, user.getPassword())) {
            throw new ValidationException("Incorrect old password for User: " + username);
        }

        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        user.setPassword(hashedPassword);
        userDAO.update(user);
        logger.info("User '{}' password changed successfully.", username);
    }
}