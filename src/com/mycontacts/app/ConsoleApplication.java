package com.mycontacts.app;

import com.mycontacts.auth.AuthenticationProvider;
import com.mycontacts.auth.SessionManager;
import com.mycontacts.service.AuthenticationService;
import com.mycontacts.service.ContactService;
import com.mycontacts.service.ProfileService;
import com.mycontacts.service.RegistrationService;
import com.mycontacts.ui.ConsoleMenus;

import java.util.Scanner;

// App runner that coordinates menu flow.
public class ConsoleApplication {
    private final Scanner scanner;
    private final SessionManager sessionManager;
    private final AuthenticationProvider basicAuth;
    private final AuthenticationProvider oauth;
    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;
    private final ProfileService profileService;
    private final ContactService contactService;

    public ConsoleApplication(
            Scanner scanner,
            SessionManager sessionManager,
            AuthenticationProvider basicAuth,
            AuthenticationProvider oauth,
            RegistrationService registrationService,
            AuthenticationService authenticationService,
            ProfileService profileService,
            ContactService contactService
    ) {
        this.scanner = scanner;
        this.sessionManager = sessionManager;
        this.basicAuth = basicAuth;
        this.oauth = oauth;
        this.registrationService = registrationService;
        this.authenticationService = authenticationService;
        this.profileService = profileService;
        this.contactService = contactService;
    }

    public void run() {
        while (true) {
            if (sessionManager.hasActiveSession()) {
                handleLoggedInMenu();
            } else if (!handleGuestMenu()) {
                System.out.println("Exiting My Contacts App.");
                return;
            }
        }
    }

    private boolean handleGuestMenu() {
        ConsoleMenus.showGuestMenu();
        switch (scanner.nextLine().trim()) {
            case "1":
                registrationService.registerUser(scanner);
                return true;
            case "2":
                authenticationService.login(scanner, basicAuth);
                return true;
            case "3":
                authenticationService.login(scanner, oauth);
                return true;
            case "0":
                return false;
            default:
                System.out.println("Invalid choice. Try again.");
                return true;
        }
    }

    private void handleLoggedInMenu() {
        profileService.displayLoggedInUser();
        ConsoleMenus.showLoggedInMenu();

        switch (scanner.nextLine().trim()) {
            case "1":
                profileService.updateProfile(scanner);
                break;
            case "2":
                profileService.changePassword(scanner);
                break;
            case "3":
                profileService.managePreferences(scanner);
                break;
            case "4":
                contactService.createContact(scanner);
                break;
            case "5":
                contactService.viewContactDetails(scanner);
                break;
            case "6":
                contactService.editContact(scanner);
                break;
            case "7":
                contactService.deleteContact(scanner);
                break;
            case "8":
                authenticationService.logout();
                break;
            default:
                System.out.println("Invalid choice. Try again.");
        }
    }
}
