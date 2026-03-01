package com.mycontacts.repository;

import com.mycontacts.user.User;
import java.util.*;

public class UserRepository {
    // In-memory user store, keyed by email.
    private final Map<String, User> usersByEmail = new HashMap<>();

    // Checks if email already exists.
    public boolean existsByEmail(String email) {
        return email != null && usersByEmail.containsKey(normalize(email));
    }

    // Saves or updates user by email key.
    public void save(User user) {
        usersByEmail.put(normalize(user.getEmail()), user);
    }

    // Updates email key safely for an existing user.
    public boolean updateEmail(User user, String newEmail) {
        if (user == null || newEmail == null) {
            return false;
        }

        String oldKey = normalize(user.getEmail());
        String newKey = normalize(newEmail);

        if (!oldKey.equals(newKey) && usersByEmail.containsKey(newKey)) {
            return false;
        }

        usersByEmail.remove(oldKey);
        user.setEmail(newEmail);
        usersByEmail.put(normalize(user.getEmail()), user);
        return true;
    }

    // Finds user by email.
    public Optional<User> findByEmail(String email) {
        return email == null
                ? Optional.empty()
                : Optional.ofNullable(usersByEmail.get(normalize(email)));
    }

    // Shared credential check for auth providers.
    public Optional<User> authenticate(String email, String passwordHash) {
        return findByEmail(email).filter(user -> user.getPasswordHash().equals(passwordHash));
    }

    // Normalizes email key format.
    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}
