package com.mycontacts.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Utility for password hashing.
public final class PasswordHasher {
    private PasswordHasher() {}

    // Hashes a password using SHA-256.
    public static String sha256(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Password cannot be null.");
        }
        try {
            // Java MessageDigest implementation.
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : out) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
