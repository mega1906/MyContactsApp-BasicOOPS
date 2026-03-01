package com.mycontacts.user;

import com.mycontacts.validation.Validators;

import java.util.UUID;

// Base user model shared by FREE and PREMIUM users.
public class User {
    // Unique identifier for each user.
    private final UUID id;
    // Basic profile fields.
    private String name;
    private String email;
    // Password is stored as SHA-256 hash only.
    private String passwordHash;
    // Account type (FREE or PREMIUM).
    private String accountType;

    // Basic preferences.
    private boolean emailNotificationsEnabled = true;
    private String preferredLanguage = "EN";
    private String contactView = "LIST";

    // Creates a new user.
    public User(String name, String email, String passwordHash, String accountType) {
        this.id = UUID.randomUUID();
        setName(name);
        setEmail(email);
        setPasswordHash(passwordHash);
        setAccountType(accountType);
    }

    // Getters and setters (JavaBeans style).
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!Validators.isValidName(name)) {
            throw new IllegalArgumentException("Name must be at least 2 characters.");
        }
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (!Validators.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.email = email.trim();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be empty.");
        }
        this.passwordHash = passwordHash;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        if (!Validators.isValidAccountType(accountType)) {
            throw new IllegalArgumentException("Account type must be FREE or PREMIUM.");
        }
        this.accountType = accountType.trim().toUpperCase();
    }

    public boolean isEmailNotificationsEnabled() {
        return emailNotificationsEnabled;
    }

    public void setEmailNotificationsEnabled(boolean emailNotificationsEnabled) {
        this.emailNotificationsEnabled = emailNotificationsEnabled;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        if (!Validators.isValidLanguage(preferredLanguage)) {
            throw new IllegalArgumentException("Language must be EN, HI, or TA.");
        }
        this.preferredLanguage = preferredLanguage.trim().toUpperCase();
    }

    public String getContactView() {
        return contactView;
    }

    public void setContactView(String contactView) {
        if (!Validators.isValidContactView(contactView)) {
            throw new IllegalArgumentException("Contact view must be LIST or CARD.");
        }
        this.contactView = contactView.trim().toUpperCase();
    }

    // Shows safe user details (without password hash).
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", accountType='" + accountType + '\'' +
                ", emailNotificationsEnabled=" + emailNotificationsEnabled +
                ", preferredLanguage='" + preferredLanguage + '\'' +
                ", contactView='" + contactView + '\'' +
                '}';
    }
}
