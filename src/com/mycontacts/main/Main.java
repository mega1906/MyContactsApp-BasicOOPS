package com.mycontacts.main;

import com.mycontacts.app.ConsoleApplication;
import com.mycontacts.auth.AuthenticationProvider;
import com.mycontacts.auth.BasicAuthProvider;
import com.mycontacts.auth.OAuthProvider;
import com.mycontacts.auth.OAuthTokenStore;
import com.mycontacts.auth.SessionManager;
import com.mycontacts.repository.ContactRepository;
import com.mycontacts.repository.UserRepository;
import com.mycontacts.service.AdvancedFilterService;
import com.mycontacts.service.AuthenticationService;
import com.mycontacts.service.BulkContactService;
import com.mycontacts.service.ContactService;
import com.mycontacts.service.ProfileService;
import com.mycontacts.service.RegistrationService;
import com.mycontacts.service.SearchService;
import com.mycontacts.service.observer.ContactDeletionNotifier;
import com.mycontacts.service.observer.DeletionAuditObserver;
import com.mycontacts.service.observer.DeletionStatsObserver;

import java.util.Scanner;

/**
 * UC10: Advanced Filtering
 *
 * User can apply multiple filters like tag, date added and frequency.
 *
 * @author Developer
 * @version 10.0
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
        SearchService searchService = new SearchService(contactRepository, sessionManager);
        BulkContactService bulkContactService = new BulkContactService(contactRepository, sessionManager, deletionNotifier);
        AdvancedFilterService advancedFilterService = new AdvancedFilterService(contactRepository, sessionManager);

        ConsoleApplication app = new ConsoleApplication(
                scanner,
                sessionManager,
                basicAuth,
                oauth,
                registrationService,
                authenticationService,
                profileService,
                contactService,
                searchService,
                bulkContactService,
                advancedFilterService
        );

        app.run();
    }
}
