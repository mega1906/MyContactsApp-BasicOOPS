package com.mycontacts.auth;

import com.mycontacts.repository.UserRepository;
import com.mycontacts.util.PasswordHasher;

import java.util.Optional;

public class BasicAuthProvider implements AuthenticationProvider {
    // Repository used to validate credentials.
    private final UserRepository userRepository;

    public BasicAuthProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<AuthSession> login(String email, String password) {
        // Basic auth: validate credentials and create session.
        return userRepository.authenticate(email, PasswordHasher.sha256(password))
                .map(user -> new AuthSession(user, "BASIC", null));
    }
}
