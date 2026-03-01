package com.mycontacts.auth;

import java.util.Optional;

public class SessionManager {
    // Only one active session is allowed.
    private AuthSession currentSession;

    public boolean startSession(AuthSession session) {
        if (currentSession != null || session == null) {
            return false;
        }
        this.currentSession = session;
        return true;
    }

    public Optional<AuthSession> getCurrentSession() {
        return Optional.ofNullable(currentSession);
    }

    // Checks if someone is logged in.
    public boolean hasActiveSession() {
        return currentSession != null;
    }

    public boolean logout() {
        if (currentSession == null) {
            return false;
        }
        currentSession = null;
        return true;
    }
}
