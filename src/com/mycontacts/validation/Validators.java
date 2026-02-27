package com.mycontacts.validation;

import java.util.regex.Pattern;

public final class Validators {
    private Validators() {}

    // Email regex to validate the input
    private static final Pattern EMAIL_RX =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_RX.matcher(email.trim()).matches();
    }

    // To check if the password satisfies the conditions
    public static boolean isStrongPassword(String pwd) {
        if (pwd == null) return false;
        // Min 8 chars, at least 1 digit and 1 letter for password validation
        boolean len = pwd.length() >= 8;
        boolean hasDigit = pwd.chars().anyMatch(Character::isDigit);
        boolean hasLetter = pwd.chars().anyMatch(Character::isLetter);
        return len && hasDigit && hasLetter;
    }
}