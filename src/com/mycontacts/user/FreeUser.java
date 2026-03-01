package com.mycontacts.user;

// Concrete FREE account user.
public class FreeUser extends User {
	// Creates a FREE user.
    public FreeUser(String name, String email, String passwordHash) {
        super(name, email, passwordHash, "FREE");
    }
}
