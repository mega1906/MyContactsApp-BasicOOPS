package com.mycontacts.user;

// Concrete PREMIUM account user.
public class PremiumUser extends User {
	// Creates a PREMIUM user.
    public PremiumUser(String name, String email, String passwordHash) {
        super(name, email, passwordHash, "PREMIUM");
    }
}
