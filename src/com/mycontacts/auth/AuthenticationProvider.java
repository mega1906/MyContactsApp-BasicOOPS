package com.mycontacts.auth;

import java.util.Optional;

public interface AuthenticationProvider {
    // Returns session when credentials are valid.
    Optional<AuthSession> login(String email, String password);
}
