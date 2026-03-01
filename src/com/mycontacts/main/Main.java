package com.mycontacts.main;

import com.mycontacts.auth.AuthSession;
import com.mycontacts.auth.AuthenticationProvider;
import com.mycontacts.auth.BasicAuthProvider;
import com.mycontacts.auth.OAuthProvider;
import com.mycontacts.auth.OAuthTokenStore;
import com.mycontacts.auth.SessionManager;
import com.mycontacts.repository.UserRepository;
import com.mycontacts.user.FreeUser;
import com.mycontacts.user.PremiumUser;
import com.mycontacts.user.User;
import com.mycontacts.util.PasswordHasher;
import com.mycontacts.validation.Validators;

import java.util.Optional;
import java.util.Scanner;

/**
 * UC-01 + UC-02: Registration and user authentication (console).
 * Supports BasicAuth/OAuth login, single active session, user details and logout.
 *
 * @author Developer
 * @version 2.0
 */
public class Main {
    // Account type constants for cleaner checks.
    private static final String FREE = "FREE";
    private static final String PREMIUM = "PREMIUM";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserRepository userRepo = new UserRepository();
        OAuthTokenStore tokenStore = new OAuthTokenStore();
        SessionManager sessionManager = new SessionManager();

        // Create auth strategies once and reuse.
        AuthenticationProvider basicAuth = new BasicAuthProvider(userRepo);
        AuthenticationProvider oauth = new OAuthProvider(userRepo, tokenStore);

        while (true) {
            if (sessionManager.hasActiveSession()) {
                handleLoggedInMenu(sc, sessionManager, tokenStore);
            } else if (!handleGuestMenu(sc, userRepo, sessionManager, basicAuth, oauth)) {
                System.out.println("Exiting My Contacts App.");
                return;
            }
        }
    }

    // Menu shown only when no one is logged in.
    private static boolean handleGuestMenu(
            Scanner sc,
            UserRepository userRepo,
            SessionManager sessionManager,
            AuthenticationProvider basicAuth,
            AuthenticationProvider oauth
    ) {
        System.out.println("\nMy Contacts App");
        System.out.println("1. Register");
        System.out.println("2. Login (Basic Auth)");
        System.out.println("3. Login (OAuth)");
        System.out.println("0. Exit");
        System.out.print("Choose option: ");

        switch (sc.nextLine().trim()) {
            case "1":
                registerUser(sc, userRepo);
                return true;
            case "2":
                loginWithProvider(sc, basicAuth, sessionManager);
                return true;
            case "3":
                loginWithProvider(sc, oauth, sessionManager);
                return true;
            case "0":
                return false;
            default:
                System.out.println("Invalid choice. Try again.");
                return true;
        }
    }

    // Menu shown only while a session is active.
    private static void handleLoggedInMenu(Scanner sc, SessionManager sessionManager, OAuthTokenStore tokenStore) {
        displayLoggedInUser(sessionManager, tokenStore);
        System.out.println("\n1. Logout");
        System.out.print("Choose option: ");

        if ("1".equals(sc.nextLine().trim())) {
            logout(sessionManager, tokenStore);
        } else {
            System.out.println("Invalid choice. Only logout is allowed while logged in.");
        }
    }

    // Registers a new user with validations.
    private static void registerUser(Scanner sc, UserRepository userRepo) {
        System.out.println("\nRegister User");
        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        String email = readValidEmail(sc, userRepo);
        String password = readValidPassword(sc);
        String type = readValidAccountType(sc);

        User user = PREMIUM.equals(type)
                ? new PremiumUser(name, email, PasswordHasher.sha256(password))
                : new FreeUser(name, email, PasswordHasher.sha256(password));

        userRepo.save(user);
        System.out.println("Registration successful!");
        System.out.println("Created: " + user);
    }

    // Gets an email that is valid and not already used.
    private static String readValidEmail(Scanner sc, UserRepository userRepo) {
        while (true) {
            System.out.print("Email: ");
            String email = sc.nextLine().trim();

            if (!Validators.isValidEmail(email)) {
                System.out.println("Invalid email. Try again.");
            } else if (userRepo.existsByEmail(email)) {
                System.out.println("Email already registered. Try again.");
            } else {
                return email;
            }
        }
    }

    // Gets a strong password with confirmation.
    private static String readValidPassword(Scanner sc) {
        while (true) {
            System.out.print("Password (min 8 chars, include letters & digits): ");
            String password = sc.nextLine();
            if (!Validators.isStrongPassword(password)) {
                System.out.println("Weak password. Try again.");
                continue;
            }

            System.out.print("Confirm Password: ");
            if (password.equals(sc.nextLine())) {
                return password;
            }
            System.out.println("Passwords do not match. Try again.");
        }
    }

    // Gets FREE or PREMIUM only.
    private static String readValidAccountType(Scanner sc) {
        while (true) {
            System.out.print("Account Type [FREE/PREMIUM]: ");
            String type = sc.nextLine().trim().toUpperCase();
            if (FREE.equals(type) || PREMIUM.equals(type)) {
                return type;
            }
            System.out.println("Please type FREE or PREMIUM.");
        }
    }

    // Common login flow for both BasicAuth and OAuth.
    private static void loginWithProvider(Scanner sc, AuthenticationProvider provider, SessionManager sessionManager) {
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

    // Prints details of currently logged-in user.
    private static void displayLoggedInUser(SessionManager sessionManager, OAuthTokenStore tokenStore) {
        Optional<AuthSession> current = sessionManager.getCurrentSession();
        if (current.isEmpty()) {
            System.out.println("No active session. Please login first.");
            return;
        }

        AuthSession session = current.get();
        User user = session.getUser();

        System.out.println("\nLogged-in User Details");
        System.out.println("Session ID: " + session.getSessionId());
        System.out.println("Login Provider: " + session.getProvider());
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Account Type: " + user.getAccountType());

        if ("OAUTH".equals(session.getProvider())) {
            String token = tokenStore.getTokenByEmail(user.getEmail()).orElse("Not found");
            System.out.println("Stored OAuth Token: " + token);
        }
    }

    // Ends the current session and clears OAuth token if needed.
    private static void logout(SessionManager sessionManager, OAuthTokenStore tokenStore) {
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
