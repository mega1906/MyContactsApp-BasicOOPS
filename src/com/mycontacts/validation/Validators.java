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

    // Name should have basic minimum length.
    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2;
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

    // Account type must be FREE or PREMIUM.
    public static boolean isValidAccountType(String accountType) {
        if (accountType == null) {
            return false;
        }
        String normalized = accountType.trim().toUpperCase();
        return "FREE".equals(normalized) || "PREMIUM".equals(normalized);
    }

    // Allowed language codes for preferences.
    public static boolean isValidLanguage(String language) {
        if (language == null) {
            return false;
        }
        String normalized = language.trim().toUpperCase();
        return "EN".equals(normalized) || "HI".equals(normalized) || "TA".equals(normalized);
    }

    // Allowed contact view modes.
    public static boolean isValidContactView(String contactView) {
        if (contactView == null) {
            return false;
        }
        String normalized = contactView.trim().toUpperCase();
        return "LIST".equals(normalized) || "CARD".equals(normalized);
    }

    // Basic phone number check.
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        String normalized = phoneNumber.trim();
        return normalized.matches("^[+0-9][0-9\\-\\s]{6,19}$");
    }

    // Tag should be readable and short.
    public static boolean isValidTagName(String tagName) {
        if (tagName == null) {
            return false;
        }
        String normalized = tagName.trim();
        return normalized.matches("^[A-Za-z][A-Za-z\\s]{1,19}$");
    }
}
