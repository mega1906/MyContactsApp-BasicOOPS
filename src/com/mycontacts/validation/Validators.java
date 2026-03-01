package com.mycontacts.validation;

import java.util.regex.Pattern;

// Validation helpers for user input.
public final class Validators {
    private Validators() {}

    // Email format check.
    private static final Pattern EMAIL_RX =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_RX.matcher(email.trim()).matches();
    }

    // Password must be 8+ chars with letters and digits.
    public static boolean isStrongPassword(String pwd) {
        if (pwd == null) {
            return false;
        }
        boolean len = pwd.length() >= 8;
        boolean hasDigit = pwd.chars().anyMatch(Character::isDigit);
        boolean hasLetter = pwd.chars().anyMatch(Character::isLetter);
        return len && hasDigit && hasLetter;
    }
}
