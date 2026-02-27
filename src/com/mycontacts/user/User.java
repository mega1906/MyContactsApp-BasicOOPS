package com.mycontacts.user;

import java.util.UUID;

/*
 * UUID to give universally unique identifier to each user
 * Name, Email, Password, Account Type are input fields
 * Passwords are stored as hashes.
 */
public class User {
    private final UUID id;
    private String name;
    private String email;
    private String passwordHash;
    // free or premium account
    private String accountType; 

    // Constructor for User class
    public User(String name, String email, String passwordHash, String accountType) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.accountType = accountType;
    }

    // Getter and Setters
    public UUID getId() { return id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    // Displays the user details
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