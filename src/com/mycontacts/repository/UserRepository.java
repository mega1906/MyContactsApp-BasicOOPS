package com.mycontacts.repository;

import com.mycontacts.user.User;
import java.util.*;

public class UserRepository {
    private final Map<String, User> usersByEmail = new HashMap<>();

    // Checks if the user exists
    public boolean existsByEmail(String email) {
        return usersByEmail.containsKey(email.toLowerCase());
    }

    // Saves the user details in the hashmap
    public void save(User user) {
        usersByEmail.put(user.getEmail().toLowerCase(), user);
    }

    // Finds user by email
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return Optional.ofNullable(usersByEmail.get(email.toLowerCase()));
    }
}