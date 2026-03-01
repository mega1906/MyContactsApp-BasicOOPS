package com.mycontacts.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OAuthTokenStore {
    // In-memory OAuth token map.
    private final Map<String, String> tokensByEmail = new HashMap<>();

    public void saveToken(String email, String token) {
        tokensByEmail.put(normalize(email), token);
    }

    public Optional<String> getTokenByEmail(String email) {
        return email == null
                ? Optional.empty()
                : Optional.ofNullable(tokensByEmail.get(normalize(email)));
    }

    public void removeTokenByEmail(String email) {
        if (email != null) {
            tokensByEmail.remove(normalize(email));
        }
    }

    // Keeps email keys consistent.
    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}
