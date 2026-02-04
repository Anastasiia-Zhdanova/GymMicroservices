package com.company.gym.util;

import java.util.Objects;

public class UsernameUtil {

    private UsernameUtil() {}

    public static String generateBaseUsername(String firstName, String lastName) {
        String first = Objects.toString(firstName, "").trim().toLowerCase();
        String last = Objects.toString(lastName, "").trim().toLowerCase();

        first = first.replaceAll("\\s", "");
        last = last.replaceAll("\\s", "");

        if (first.isEmpty() && last.isEmpty()) {
            throw new IllegalArgumentException("First name and last name cannot be empty.");
        }

        if (first.isEmpty()) {
            return last;
        }
        if (last.isEmpty()) {
            return first;
        }

        return first + "." + last;
    }
}