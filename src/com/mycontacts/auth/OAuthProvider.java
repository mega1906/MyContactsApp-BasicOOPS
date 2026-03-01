package com.mycontacts.auth;

import com.mycontacts.repository.UserRepository;
import com.mycontacts.util.PasswordHasher;

import java.util.Optional;
import java.util.UUID;

public class OAuthProvider implements AuthenticationProvider {
    // Dependencies for user validation and token storage.
    private final UserRepository userRepository;
    private final OAuthTokenStore tokenStore;

    public OAuthProvider(UserRepository userRepository, OAuthTokenStore tokenStore) {
        this.userRepository = userRepository;
        this.tokenStore = tokenStore;
    }

    @Override
    public Optional<AuthSession> login(String email, String password) {
        // OAuth flow: validate then issue/store random token.
        return userRepository.authenticate(email, PasswordHasher.sha256(password))
                .map(user -> {
                    String token = UUID.randomUUID().toString();
                    tokenStore.saveToken(user.getEmail(), token);
                    return new AuthSession(user, "OAUTH", token);
                });
    }
}
