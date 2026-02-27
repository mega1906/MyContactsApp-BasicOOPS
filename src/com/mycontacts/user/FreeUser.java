package com.mycontacts.user;

public class FreeUser extends User {
	// Constructor to create a Free User
    public FreeUser(String name, String email, String passwordHash) {
        super(name, email, passwordHash, "FREE");
    }
}