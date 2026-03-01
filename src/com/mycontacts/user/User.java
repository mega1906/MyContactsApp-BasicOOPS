package com.mycontacts.user;

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

    // Creates a new user.
    public User(String name, String email, String passwordHash, String accountType) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.accountType = accountType;
    }

    // Getters and setters.
    public UUID getId() { return id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    // Shows safe user details (without password hash).
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", accountType='" + accountType + '\'' +
                '}';
    }
}
