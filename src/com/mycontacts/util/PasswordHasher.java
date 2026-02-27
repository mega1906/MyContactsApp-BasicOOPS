package com.mycontacts.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordHasher {
    private PasswordHasher() {}

    // Password hashing method to hash the password and store it
    public static String sha256(String input) {
    	if (input == null) throw new IllegalArgumentException("Password cannot be null.");
        try {
        	// Message Digest to hash the password
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : out) sb.append(String.format("%02x", b));
            return sb.toString();
        } 
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
