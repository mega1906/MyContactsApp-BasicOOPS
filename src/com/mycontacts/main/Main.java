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
import com.mycontacts.service.BulkContactService;
import com.mycontacts.service.ContactService;
import com.mycontacts.service.ProfileService;
import com.mycontacts.service.RegistrationService;
import com.mycontacts.service.observer.ContactDeletionNotifier;
import com.mycontacts.service.observer.DeletionAuditObserver;
import com.mycontacts.service.observer.DeletionStatsObserver;

import java.util.Scanner;

/**
 * UC8: Bulk Operations
 *
 * User can run bulk delete, bulk tag and bulk export on contacts.
 *
 * @author Developer
 * @version 8.0
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
        ContactDeletionNotifier deletionNotifier = new ContactDeletionNotifier();
        deletionNotifier.registerObserver(new DeletionAuditObserver());
        deletionNotifier.registerObserver(new DeletionStatsObserver());
        ContactService contactService = new ContactService(contactRepository, sessionManager, deletionNotifier);
        BulkContactService bulkContactService = new BulkContactService(contactRepository, sessionManager, deletionNotifier);

        ConsoleApplication app = new ConsoleApplication(
                scanner,
                sessionManager,
                basicAuth,
                oauth,
                registrationService,
                authenticationService,
                profileService,
                contactService,
                bulkContactService
        );

        app.run();
    }
}
