package com.mycontacts.service;

import com.mycontacts.auth.AuthSession;
import com.mycontacts.auth.AuthenticationProvider;
import com.mycontacts.auth.OAuthTokenStore;
import com.mycontacts.auth.SessionManager;

import java.util.Optional;
import java.util.Scanner;

// Handles login and logout behavior.
public class AuthenticationService {
    private final SessionManager sessionManager;
    private final OAuthTokenStore tokenStore;

    public AuthenticationService(SessionManager sessionManager, OAuthTokenStore tokenStore) {
        this.sessionManager = sessionManager;
        this.tokenStore = tokenStore;
    }

    public void login(Scanner sc, AuthenticationProvider provider) {
        if (sessionManager.hasActiveSession()) {
            System.out.println("A user is already logged in. Please logout first.");
            return;
        }

        System.out.println("\nLogin");
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine();

        Optional<AuthSession> authResult = provider.login(email, password);
        if (authResult.isEmpty()) {
            System.out.println("Login failed. Invalid credentials.");
            return;
        }

        AuthSession session = authResult.get();
        if (!sessionManager.startSession(session)) {
            System.out.println("Unable to create session. A user is already logged in.");
            return;
        }

        System.out.println("Login successful using " + session.getProvider() + ".");
        session.getOauthToken().ifPresent(token -> System.out.println("OAuth token issued: " + token));
    }

    public void logout() {
        Optional<AuthSession> current = sessionManager.getCurrentSession();
        if (current.isEmpty()) {
            System.out.println("No active session to logout.");
            return;
        }

        AuthSession session = current.get();
        if ("OAUTH".equals(session.getProvider())) {
            tokenStore.removeTokenByEmail(session.getUser().getEmail());
        }

        sessionManager.logout();
        System.out.println("Logout successful.");
    }
}
