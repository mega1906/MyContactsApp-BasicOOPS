package com.mycontacts.contact;

import com.mycontacts.validation.Validators;

// Value object for contact phone number.
public class PhoneNumber {
    private final String label;
    private final String number;

    public PhoneNumber(String label, String number) {
        if (!Validators.isValidPhoneNumber(number)) {
            throw new IllegalArgumentException("Invalid phone number format.");
        }
        this.label = (label == null || label.isBlank()) ? "OTHER" : label.trim().toUpperCase();
        this.number = number.trim();
    }

    public String getLabel() {
        return label;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return label + ": " + number;
    }
}
