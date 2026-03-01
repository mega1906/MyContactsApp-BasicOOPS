package com.mycontacts.service;

import com.mycontacts.auth.AuthSession;
import com.mycontacts.auth.OAuthTokenStore;
import com.mycontacts.auth.SessionManager;
import com.mycontacts.repository.UserRepository;
import com.mycontacts.user.User;
import com.mycontacts.util.PasswordHasher;
import com.mycontacts.validation.Validators;

import java.util.Optional;
import java.util.Scanner;

// Handles profile and preference management.
public class ProfileService {
    private final UserRepository userRepository;
    private final OAuthTokenStore tokenStore;
    private final SessionManager sessionManager;

    public ProfileService(UserRepository userRepository, OAuthTokenStore tokenStore, SessionManager sessionManager) {
        this.userRepository = userRepository;
        this.tokenStore = tokenStore;
        this.sessionManager = sessionManager;
    }

    public void displayLoggedInUser() {
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
        System.out.println("Email Notifications: " + (user.isEmailNotificationsEnabled() ? "ON" : "OFF"));
        System.out.println("Preferred Language: " + user.getPreferredLanguage());
        System.out.println("Contact View: " + user.getContactView());

        if ("OAUTH".equals(session.getProvider())) {
            String token = tokenStore.getTokenByEmail(user.getEmail()).orElse("Not found");
            System.out.println("Stored OAuth Token: " + token);
        }
    }

    public void updateProfile(Scanner sc) {
        Optional<AuthSession> current = sessionManager.getCurrentSession();
        if (current.isEmpty()) {
            System.out.println("No active session.");
            return;
        }

        AuthSession session = current.get();
        User user = session.getUser();

        System.out.println("\nUpdate Profile");
        System.out.println("1. Update Name");
        System.out.println("2. Update Email");
        System.out.print("Choose option: ");

        String choice = sc.nextLine().trim();
        if ("1".equals(choice)) {
            System.out.print("New Name: ");
            try {
                user.setName(sc.nextLine());
                System.out.println("Name updated successfully.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
            return;
        }

        if (!"2".equals(choice)) {
            System.out.println("Invalid choice.");
            return;
        }

        System.out.print("New Email: ");
        String newEmail = sc.nextLine().trim();
        String oldEmail = user.getEmail();

        try {
            boolean updated = userRepository.updateEmail(user, newEmail);
            if (!updated) {
                System.out.println("Email already registered. Try another email.");
                return;
            }

            if ("OAUTH".equals(session.getProvider())) {
                String token = tokenStore.getTokenByEmail(oldEmail).orElse(null);
                tokenStore.removeTokenByEmail(oldEmail);
                if (token != null) {
                    tokenStore.saveToken(user.getEmail(), token);
                }
            }

            System.out.println("Email updated successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void changePassword(Scanner sc) {
        Optional<AuthSession> current = sessionManager.getCurrentSession();
        if (current.isEmpty()) {
            System.out.println("No active session.");
            return;
        }

        User user = current.get().getUser();

        System.out.println("\nChange Password");
        System.out.print("Current Password: ");
        String currentPassword = sc.nextLine();
        if (!PasswordHasher.sha256(currentPassword).equals(user.getPasswordHash())) {
            System.out.println("Current password is incorrect.");
            return;
        }

        System.out.print("New Password (min 8 chars, include letters & digits): ");
        String newPassword = sc.nextLine();
        if (!Validators.isStrongPassword(newPassword)) {
            System.out.println("Weak password. Password not changed.");
            return;
        }

        String newHash = PasswordHasher.sha256(newPassword);
        if (newHash.equals(user.getPasswordHash())) {
            System.out.println("New password must be different from old password.");
            return;
        }

        System.out.print("Confirm New Password: ");
        if (!newPassword.equals(sc.nextLine())) {
            System.out.println("Passwords do not match. Password not changed.");
            return;
        }

        user.setPasswordHash(newHash);
        System.out.println("Password updated successfully.");
    }

    public void managePreferences(Scanner sc) {
        Optional<AuthSession> current = sessionManager.getCurrentSession();
        if (current.isEmpty()) {
            System.out.println("No active session.");
            return;
        }

        User user = current.get().getUser();

        System.out.println("\nManage Preferences");
        System.out.println("1. Toggle Email Notifications (Current: "
                + (user.isEmailNotificationsEnabled() ? "ON" : "OFF") + ")");
        System.out.println("2. Set Preferred Language (Current: " + user.getPreferredLanguage() + ")");
        System.out.println("3. Set Contact View (Current: " + user.getContactView() + ")");
        System.out.print("Choose option: ");

        String choice = sc.nextLine().trim();
        try {
            if ("1".equals(choice)) {
                user.setEmailNotificationsEnabled(!user.isEmailNotificationsEnabled());
                System.out.println("Email notifications are now "
                        + (user.isEmailNotificationsEnabled() ? "ON" : "OFF") + ".");
            } else if ("2".equals(choice)) {
                System.out.print("Enter language [EN/HI/TA]: ");
                user.setPreferredLanguage(sc.nextLine());
                System.out.println("Preferred language updated.");
            } else if ("3".equals(choice)) {
                System.out.print("Enter contact view [LIST/CARD]: ");
                user.setContactView(sc.nextLine());
                System.out.println("Contact view updated.");
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
