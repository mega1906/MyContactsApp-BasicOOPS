package com.mycontacts.contact;

import com.mycontacts.validation.Validators;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// Base contact model with common fields.
public abstract class Contact {
    private final UUID id;
    private final String referenceId;
    private final UUID ownerUserId;

    private String name;
    private String address;
    private String notes;

    // Composition: one contact can have many phone/email values.
    private final List<PhoneNumber> phoneNumbers = new ArrayList<>();
    private final List<EmailAddress> emailAddresses = new ArrayList<>();

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected Contact(UUID ownerUserId, String referenceId, String name) {
        if (ownerUserId == null) {
            throw new IllegalArgumentException("Owner id cannot be null.");
        }
        if (referenceId == null || referenceId.isBlank()) {
            throw new IllegalArgumentException("Reference id cannot be blank.");
        }
        if (!Validators.isValidName(name)) {
            throw new IllegalArgumentException("Contact name must be at least 2 characters.");
        }

        this.id = UUID.randomUUID();
        this.referenceId = referenceId.trim().toUpperCase();
        this.ownerUserId = ownerUserId;
        this.name = name.trim();

        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public UUID getId() {
        return id;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!Validators.isValidName(name)) {
            throw new IllegalArgumentException("Contact name must be at least 2 characters.");
        }
        this.name = name.trim();
        touch();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
        touch();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? null : notes.trim();
        touch();
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return Collections.unmodifiableList(phoneNumbers);
    }

    public List<EmailAddress> getEmailAddresses() {
        return Collections.unmodifiableList(emailAddresses);
    }

    public void addPhoneNumber(PhoneNumber phoneNumber) {
        if (phoneNumber == null) {
            throw new IllegalArgumentException("Phone number cannot be null.");
        }
        phoneNumbers.add(phoneNumber);
        touch();
    }

    public void addEmailAddress(EmailAddress emailAddress) {
        if (emailAddress == null) {
            throw new IllegalArgumentException("Email address cannot be null.");
        }
        emailAddresses.add(emailAddress);
        touch();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    protected void touch() {
        updatedAt = LocalDateTime.now();
    }

    public abstract String getContactType();

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", referenceId='" + referenceId + '\'' +
                ", type='" + getContactType() + '\'' +
                ", name='" + name + '\'' +
                ", phones=" + phoneNumbers +
                ", emails=" + emailAddresses +
                ", createdAt=" + createdAt +
                '}';
    }
}
