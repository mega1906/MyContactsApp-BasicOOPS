package com.mycontacts.main;

import com.mycontacts.app.ConsoleApplication;
import com.mycontacts.auth.AuthenticationProvider;
import com.mycontacts.auth.BasicAuthProvider;
import com.mycontacts.auth.OAuthProvider;
import com.mycontacts.auth.OAuthTokenStore;
import com.mycontacts.auth.SessionManager;
import com.mycontacts.repository.ContactRepository;
import com.mycontacts.repository.UserRepository;
import com.mycontacts.service.AuthenticationService;
import com.mycontacts.service.ContactService;
import com.mycontacts.service.ProfileService;
import com.mycontacts.service.RegistrationService;

import java.util.Scanner;

/**
 * UC4: Create Contact
 * 
 * User can create new contacts and save them
 * 
 * @author Developer
 * @version 4.0
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        UserRepository userRepository = new UserRepository();
        ContactRepository contactRepository = new ContactRepository();
        OAuthTokenStore tokenStore = new OAuthTokenStore();
        SessionManager sessionManager = new SessionManager();

        AuthenticationProvider basicAuth = new BasicAuthProvider(userRepository);
        AuthenticationProvider oauth = new OAuthProvider(userRepository, tokenStore);

        RegistrationService registrationService = new RegistrationService(userRepository);
        AuthenticationService authenticationService = new AuthenticationService(sessionManager, tokenStore);
        ProfileService profileService = new ProfileService(userRepository, tokenStore, sessionManager);
        ContactService contactService = new ContactService(contactRepository, sessionManager);

        ConsoleApplication app = new ConsoleApplication(
                scanner,
                sessionManager,
                basicAuth,
                oauth,
                registrationService,
                authenticationService,
                profileService,
                contactService
        );

        app.run();
    }
}
