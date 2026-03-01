package com.mycontacts.auth;

import com.mycontacts.user.User;

import java.util.Optional;
import java.util.UUID;

public class AuthSession {
    // Unique id for active login session.
    private final UUID sessionId;
    // User attached to the session.
    private final User user;
    // Auth provider used to login (BASIC/OAUTH).
    private final String provider;
    // Present only for OAuth logins.
    private final String oauthToken;

    public AuthSession(User user, String provider, String oauthToken) {
        this.sessionId = UUID.randomUUID();
        this.user = user;
        this.provider = provider;
        this.oauthToken = oauthToken;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public User getUser() {
        return user;
    }

    public String getProvider() {
        return provider;
    }

    public Optional<String> getOauthToken() {
        return Optional.ofNullable(oauthToken);
    }
}
