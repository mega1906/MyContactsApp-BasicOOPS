package com.mycontacts.service;

import com.mycontacts.auth.AuthSession;
import com.mycontacts.auth.SessionManager;
import com.mycontacts.contact.Contact;
import com.mycontacts.filter.CompositeContactFilter;
import com.mycontacts.filter.ContactFilter;
import com.mycontacts.filter.DateAddedAfterFilter;
import com.mycontacts.filter.FrequentlyContactedFilter;
import com.mycontacts.filter.TagContactFilter;
import com.mycontacts.repository.ContactRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

// Handles UC-10 advanced filtering.
public class AdvancedFilterService {
    private final ContactRepository contactRepository;
    private final SessionManager sessionManager;

    public AdvancedFilterService(ContactRepository contactRepository, SessionManager sessionManager) {
        this.contactRepository = contactRepository;
        this.sessionManager = sessionManager;
    }

    public void runAdvancedFiltering(Scanner sc) {
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

        ContactFilter filter = buildFilter(sc);
        Comparator<Contact> comparator = chooseSort(sc);

        List<Contact> results = contacts.stream()
                .filter(filter.toPredicate())
                .sorted(comparator)
                .toList();

        printResults(results);
    }

    private ContactFilter buildFilter(Scanner sc) {
        List<ContactFilter> filters = new ArrayList<>();

        // Keep input simple: blank means skip.
        System.out.print("Filter by tag (blank to skip): ");
        String tag = sc.nextLine().trim();
        if (!tag.isBlank()) {
            filters.add(new TagContactFilter(tag));
        }

        System.out.print("Filter by date added from (YYYY-MM-DD, blank to skip): ");
        String dateText = sc.nextLine().trim();
        if (!dateText.isBlank()) {
            try {
                filters.add(new DateAddedAfterFilter(LocalDate.parse(dateText)));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Date filter skipped.");
            }
        }

        System.out.print("Minimum contacted count (blank to skip): ");
        String countText = sc.nextLine().trim();
        if (!countText.isBlank()) {
            try {
                int min = Integer.parseInt(countText);
                if (min >= 0) {
                    filters.add(new FrequentlyContactedFilter(min));
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Frequency filter skipped.");
            }
        }

        if (filters.isEmpty()) {
            return contact -> true;
        }
        return new CompositeContactFilter(filters);
    }

    private Comparator<Contact> chooseSort(Scanner sc) {
        System.out.println("\nSort Results");
        System.out.println("1. Name (A-Z)");
        System.out.println("2. Date Added (Newest First)");
        System.out.println("3. Frequently Contacted (High to Low)");
        System.out.print("Choose sort: ");

        switch (sc.nextLine().trim()) {
            case "1":
                return Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Contact::getReferenceId);
            case "2":
                return Comparator.comparing(Contact::getCreatedAt).reversed()
                        .thenComparing(Contact::getName, String.CASE_INSENSITIVE_ORDER);
            case "3":
                return Comparator.comparingInt(Contact::getContactedCount).reversed()
                        .thenComparing(Contact::getName, String.CASE_INSENSITIVE_ORDER);
            default:
                System.out.println("Invalid sort. Using Name (A-Z).");
                return Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER);
        }
    }

    private void printResults(List<Contact> results) {
        if (results.isEmpty()) {
            System.out.println("No contacts matched the applied filters.");
            return;
        }

        System.out.println("Filtered contacts: " + results.size());
        results.forEach(this::printContactLine);
    }

    private void printContactLine(Contact contact) {
        String tags = contact.getTags().stream().sorted().collect(Collectors.joining("|"));
        System.out.println("- " + contact.getReferenceId()
                + " | " + contact.getName()
                + " | added: " + contact.getCreatedAt().toLocalDate()
                + " | contacted: " + contact.getContactedCount()
                + " | tags: " + (tags.isBlank() ? "N/A" : tags));
    }
}
