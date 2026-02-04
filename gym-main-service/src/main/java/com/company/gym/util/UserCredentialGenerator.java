package com.company.gym.util;

import java.util.UUID;

public final class UserCredentialGenerator {

    private UserCredentialGenerator() {}

    public static String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 10);
    }
}