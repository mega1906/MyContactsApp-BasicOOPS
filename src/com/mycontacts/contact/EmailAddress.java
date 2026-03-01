package com.mycontacts.contact;

import com.mycontacts.validation.Validators;

// Value object for contact email address.
public class EmailAddress {
    private final String label;
    private final String email;

    public EmailAddress(String label, String email) {
        if (!Validators.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.label = (label == null || label.isBlank()) ? "OTHER" : label.trim().toUpperCase();
        this.email = email.trim();
    }

    public String getLabel() {
        return label;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return label + ": " + email;
    }
}
