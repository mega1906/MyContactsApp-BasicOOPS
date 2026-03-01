package com.mycontacts.service;

import com.mycontacts.auth.AuthSession;
import com.mycontacts.auth.SessionManager;
import com.mycontacts.contact.Contact;
import com.mycontacts.repository.ContactRepository;
import com.mycontacts.search.EmailSearchCriteria;
import com.mycontacts.search.NameSearchCriteria;
import com.mycontacts.search.PhoneSearchCriteria;
import com.mycontacts.search.SearchCriteria;
import com.mycontacts.search.TagSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

// Handles UC-09 search contacts.
public class SearchService {
    private final ContactRepository contactRepository;
    private final SessionManager sessionManager;

    public SearchService(ContactRepository contactRepository, SessionManager sessionManager) {
        this.contactRepository = contactRepository;
        this.sessionManager = sessionManager;
    }

    public void searchContacts(Scanner sc) {
        Optional<AuthSession> current = sessionManager.getCurrentSession();
        if (current.isEmpty()) {
            System.out.println("No active session.");
            return;
        }

        UUID ownerId = current.get().getUser().getId();
        List<Contact> contacts = contactRepository.findByOwnerUserId(ownerId);
        if (contacts.isEmpty()) {
            System.out.println("No contacts found for this user.");
            return;
        }

        System.out.println("\nSearch Contacts");
        System.out.println("Regex is supported. Search is case-insensitive.");
        System.out.println("1. Search by Name");
        System.out.println("2. Search by Phone");
        System.out.println("3. Search by Email");
        System.out.println("4. Search by Tag");
        System.out.println("5. Display All Saved Contacts");
        System.out.print("Choose option: ");

        String choice = sc.nextLine().trim();
        if ("5".equals(choice)) {
            printResults(contacts);
            return;
        }

        SearchCriteria criteria = buildCriteria(sc, choice);
        if (criteria == null) {
            System.out.println("Invalid search choice.");
            return;
        }

        List<Contact> results = contacts.stream()
                .filter(criteria.toPredicate())
                .toList();

        printResults(results);
    }

    private SearchCriteria buildCriteria(Scanner sc, String choice) {
        switch (choice) {
            case "1":
                System.out.print("Name query: ");
                return new NameSearchCriteria(sc.nextLine().trim());
            case "2":
                System.out.print("Phone query: ");
                return new PhoneSearchCriteria(sc.nextLine().trim());
            case "3":
                System.out.print("Email query: ");
                return new EmailSearchCriteria(sc.nextLine().trim());
            case "4":
                System.out.print("Tag query: ");
                return new TagSearchCriteria(sc.nextLine().trim());
            default:
                return null;
        }
    }

    private void printResults(List<Contact> results) {
        if (results.isEmpty()) {
            System.out.println("No contacts matched.");
            return;
        }

        System.out.println("Matched contacts: " + results.size());
        results.forEach(this::printContactLine);
    }

    private void printContactLine(Contact contact) {
        String phones = contact.getPhoneNumbers().stream()
                .map(Object::toString)
                .collect(Collectors.joining(" | "));
        String emails = contact.getEmailAddresses().stream()
                .map(Object::toString)
                .collect(Collectors.joining(" | "));
        String tags = contact.getTags().stream().sorted().collect(Collectors.joining(" | "));

        System.out.println("- " + contact.getReferenceId()
                + " | " + contact.getName()
                + " | phones: " + phones
                + " | emails: " + emails
                + " | tags: " + (tags.isBlank() ? "N/A" : tags));
    }
}
