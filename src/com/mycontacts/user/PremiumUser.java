package com.mycontacts.user;

public class PremiumUser extends User {
	// Constructor to create a Premium user
    public PremiumUser(String name, String email, String passwordHash) {
        super(name, email, passwordHash, "PREMIUM");
    }
}