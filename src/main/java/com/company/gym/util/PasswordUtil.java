package com.company.gym.util;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordUtil {
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);

    private PasswordUtil() {}

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }

        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        logger.debug("Password hashed successfully.");
        return hashedPassword;
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        try {
            boolean match = BCrypt.checkpw(plainPassword, hashedPassword);
            logger.debug("Password check result: {}", match);
            return match;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid hashed password format provided for checking.", e);
            return false;
        }
    }
}