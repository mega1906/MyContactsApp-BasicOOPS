package com.mycontacts.service;

import com.mycontacts.auth.AuthSession;
import com.mycontacts.auth.SessionManager;
import com.mycontacts.contact.Contact;
import com.mycontacts.repository.ContactRepository;
import com.mycontacts.service.observer.ContactDeletionNotifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Handles UC-08 bulk operations (delete, tag, export).
public class BulkContactService {
    private final ContactRepository contactRepository;
    private final SessionManager sessionManager;
    private final ContactDeletionNotifier deletionNotifier;

    public BulkContactService(
            ContactRepository contactRepository,
            SessionManager sessionManager,
            ContactDeletionNotifier deletionNotifier
    ) {
        this.contactRepository = contactRepository;
        this.sessionManager = sessionManager;
        this.deletionNotifier = deletionNotifier;
    }

    public void openBulkOperationsMenu(java.util.Scanner sc) {
        Optional<AuthSession> current = sessionManager.getCurrentSession();
        if (current.isEmpty()) {
            System.out.println("No active session.");
            return;
        }

        UUID ownerId = current.get().getUser().getId();
        while (true) {
            System.out.println("\nBulk Operations");
            System.out.println("1. Bulk Delete");
            System.out.println("2. Bulk Tag");
            System.out.println("3. Bulk Export (CSV)");
            System.out.println("0. Back");
            System.out.print("Choose option: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    bulkDelete(sc, ownerId);
                    break;
                case "2":
                    bulkTag(sc, ownerId);
                    break;
                case "3":
                    bulkExport(sc, ownerId);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void bulkDelete(java.util.Scanner sc, UUID ownerId) {
        List<Contact> selected = selectContacts(sc, ownerId);
        if (selected.isEmpty()) {
            return;
        }

        if (!askYesNo(sc, "Delete " + selected.size() + " contacts? [Y/N]: ")) {
            System.out.println("Bulk delete cancelled.");
            return;
        }

        long deleted = selected.stream()
                .map(Contact::getReferenceId)
                .map(ref -> contactRepository.deleteByReferenceIdAndOwnerUserId(ref, ownerId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(deletionNotifier::notifyDeleted)
                .count();

        System.out.println("Bulk delete completed. Deleted: " + deleted);
    }

    private void bulkTag(java.util.Scanner sc, UUID ownerId) {
        List<Contact> selected = selectContacts(sc, ownerId);
        if (selected.isEmpty()) {
            return;
        }

        System.out.print("Enter tag to apply: ");
        String tag = sc.nextLine();
        try {
            selected.forEach(contact -> contact.addTag(tag));
            System.out.println("Tag applied to " + selected.size() + " contacts.");
        } catch (IllegalArgumentException e) {
            System.out.println("Bulk tag failed: " + e.getMessage());
        }
    }

    private void bulkExport(java.util.Scanner sc, UUID ownerId) {
        List<Contact> selected = selectContacts(sc, ownerId);
        if (selected.isEmpty()) {
            return;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        Path exportDir = Path.of("exports");
        Path outputFile = exportDir.resolve("contacts-bulk-" + timestamp + ".csv");

        List<String> lines = Stream.concat(
                Stream.of("ref_id,type,name,phones,emails,tags,address,notes,created_at,updated_at"),
                selected.stream().map(this::toCsvLine)
        ).toList();

        try {
            Files.createDirectories(exportDir);
            Files.write(outputFile, lines, StandardCharsets.UTF_8);
            System.out.println("Bulk export successful: " + outputFile.toAbsolutePath());
            System.out.println("Exported contacts: " + selected.size());
        } catch (IOException e) {
            System.out.println("Bulk export failed: " + e.getMessage());
        }
    }

    private List<Contact> selectContacts(java.util.Scanner sc, UUID ownerId) {
        List<Contact> contacts = contactRepository.findByOwnerUserId(ownerId);
        if (contacts.isEmpty()) {
            System.out.println("No contacts found for this user.");
            return List.of();
        }

        System.out.println("\nSelect Contacts Filter");
        System.out.println("1. By Ref IDs (comma separated)");
        System.out.println("2. By Contact Type");
        System.out.println("3. By Existing Tag");
        System.out.println("4. By Name Contains");
        System.out.println("5. All Contacts");
        System.out.print("Choose filter: ");

        Predicate<Contact> predicate = buildFilterPredicate(sc, contacts, sc.nextLine().trim());
        List<Contact> selected = contacts.stream().filter(predicate).toList();

        if (selected.isEmpty()) {
            System.out.println("No contacts matched the filter.");
            return List.of();
        }

        String refs = selected.stream().map(Contact::getReferenceId).collect(Collectors.joining(", "));
        System.out.println("Selected contacts: " + refs);
        return selected;
    }

    private Predicate<Contact> buildFilterPredicate(java.util.Scanner sc, List<Contact> contacts, String choice) {
        switch (choice) {
            case "1":
                System.out.print("Enter Ref IDs (example ALI1,ALI2): ");
                Set<String> ids = Arrays.stream(sc.nextLine().split(","))
                        .map(String::trim)
                        .map(String::toUpperCase)
                        .filter(value -> !value.isBlank())
                        .collect(Collectors.toSet());
                return contact -> ids.contains(contact.getReferenceId());
            case "2":
                System.out.print("Enter Type [PERSON/ORGANIZATION]: ");
                String type = sc.nextLine().trim().toUpperCase();
                return contact -> type.equals(contact.getContactType());
            case "3":
                System.out.print("Enter Tag: ");
                String tag = sc.nextLine().trim().toUpperCase();
                return contact -> contact.getTags().contains(tag);
            case "4":
                System.out.print("Name contains: ");
                String term = sc.nextLine().trim().toLowerCase();
                return contact -> contact.getName().toLowerCase().contains(term);
            case "5":
                return contact -> true;
            default:
                System.out.println("Invalid filter. Using all contacts.");
                return contact -> true;
        }
    }

    private String toCsvLine(Contact contact) {
        String phones = contact.getPhoneNumbers().stream().map(Object::toString).collect(Collectors.joining("|"));
        String emails = contact.getEmailAddresses().stream().map(Object::toString).collect(Collectors.joining("|"));
        String tags = contact.getTags().stream().sorted().collect(Collectors.joining("|"));

        return Stream.of(
                contact.getReferenceId(),
                contact.getContactType(),
                contact.getName(),
                phones,
                emails,
                tags,
                Optional.ofNullable(contact.getAddress()).orElse(""),
                Optional.ofNullable(contact.getNotes()).orElse(""),
                String.valueOf(contact.getCreatedAt()),
                String.valueOf(contact.getUpdatedAt())
        ).map(this::escapeCsv).collect(Collectors.joining(","));
    }

    private String escapeCsv(String value) {
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private boolean askYesNo(java.util.Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = sc.nextLine().trim().toUpperCase();
            if ("Y".equals(answer)) {
                return true;
            }
            if ("N".equals(answer)) {
                return false;
            }
            System.out.println("Please enter Y or N.");
        }
    }
}
