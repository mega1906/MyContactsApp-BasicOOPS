package com.mycontacts.service;

import com.mycontacts.auth.AuthSession;
import com.mycontacts.auth.SessionManager;
import com.mycontacts.contact.Contact;
import com.mycontacts.contact.ContactDetailsView;
import com.mycontacts.contact.EmailAddress;
import com.mycontacts.contact.OrganizationContact;
import com.mycontacts.contact.PersonContact;
import com.mycontacts.contact.PhoneNumber;
import com.mycontacts.repository.ContactRepository;
import com.mycontacts.service.exception.ContactDeleteException;
import com.mycontacts.service.observer.ContactDeletionNotifier;
import com.mycontacts.user.User;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

// Handles UC-04 to UC-07 for contacts.
public class ContactService {
    private final ContactRepository contactRepository;
    private final SessionManager sessionManager;
    private final ContactDeletionNotifier deletionNotifier;

    public ContactService(
            ContactRepository contactRepository,
            SessionManager sessionManager,
            ContactDeletionNotifier deletionNotifier
    ) {
        this.contactRepository = contactRepository;
        this.sessionManager = sessionManager;
        this.deletionNotifier = deletionNotifier;
    }

    public void createContact(Scanner sc) {
        Optional<AuthSession> current = sessionManager.getCurrentSession();
        if (current.isEmpty()) {
            System.out.println("No active session.");
            return;
        }

        User owner = current.get().getUser();
        UUID ownerId = owner.getId();
        String referenceId = contactRepository.nextReferenceId(ownerId, owner.getName());

        System.out.println("\nCreate Contact");
        System.out.print("Contact Type [PERSON/ORGANIZATION]: ");
        String type = sc.nextLine().trim().toUpperCase();

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        Contact contact;
        try {
            if ("PERSON".equals(type)) {
                PersonContact person = new PersonContact(ownerId, referenceId, name);
                System.out.print("Relationship (optional): ");
                person.setRelationship(sc.nextLine());
                contact = person;
            } else if ("ORGANIZATION".equals(type)) {
                OrganizationContact organization = new OrganizationContact(ownerId, referenceId, name);
                System.out.print("Organization Type (optional): ");
                organization.setOrganizationType(sc.nextLine());
                contact = organization;
            } else {
                System.out.println("Invalid contact type.");
                return;
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (!readPhoneNumbers(sc, contact) || !readEmailAddresses(sc, contact)) {
            return;
        }

        System.out.print("Address (optional): ");
        contact.setAddress(sc.nextLine());

        System.out.print("Notes (optional): ");
        contact.setNotes(sc.nextLine());

        contactRepository.save(contact);
        System.out.println("Contact created successfully!");
        System.out.println("Contact Ref ID: " + contact.getReferenceId());
        System.out.println("Created At: " + contact.getCreatedAt());
    }

    // UC-05: view complete details of one contact.
    public void viewContactDetails(Scanner sc) {
        Optional<AuthSession> current = sessionManager.getCurrentSession();
        if (current.isEmpty()) {
            System.out.println("No active session.");
            return;
        }

        UUID ownerId = current.get().getUser().getId();
        java.util.List<Contact> contacts = contactRepository.findByOwnerUserId(ownerId);
        if (contacts.isEmpty()) {
            System.out.println("No contacts found for this user.");
            return;
        }

        String availableRefs = contacts.stream()
                .map(Contact::getReferenceId)
                .collect(Collectors.joining(", "));
        System.out.println("Available Contact Ref IDs: " + availableRefs);
        System.out.print("Enter Contact Ref ID (example ALI1): ");
        String referenceId = sc.nextLine().trim();

        Optional<Contact> contact = contactRepository.findByReferenceIdAndOwnerUserId(referenceId, ownerId);
        if (contact.isEmpty()) {
            System.out.println("Contact not found.");
            return;
        }

        ContactDetailsView detailsView = ContactDetailsView.from(contact.get());
        System.out.println();
        System.out.println(detailsView);
    }

    // UC-06: edit an existing contact through a copied draft object.
    public void editContact(Scanner sc) {
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

        String availableRefs = contacts.stream()
                .map(Contact::getReferenceId)
                .collect(Collectors.joining(", "));
        System.out.println("Available Contact Ref IDs: " + availableRefs);
        System.out.print("Enter Contact Ref ID to edit: ");
        String referenceId = sc.nextLine().trim();

        Optional<Contact> original = contactRepository.findByReferenceIdAndOwnerUserId(referenceId, ownerId);
        if (original.isEmpty()) {
            System.out.println("Contact not found.");
            return;
        }

        Contact draft = copyForEdit(original.get());
        boolean save = editDraft(sc, draft);
        if (!save) {
            System.out.println("Edit cancelled. No changes saved.");
            return;
        }

        boolean updated = contactRepository.replaceByReferenceIdAndOwnerUserId(referenceId, ownerId, draft);
        if (updated) {
            System.out.println("Contact updated successfully.");
        } else {
            System.out.println("Unable to update contact.");
        }
    }

    // UC-07: delete contact with confirmation.
    public void deleteContact(Scanner sc) {
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

        String availableRefs = contacts.stream()
                .map(Contact::getReferenceId)
                .collect(Collectors.joining(", "));
        System.out.println("Available Contact Ref IDs: " + availableRefs);
        System.out.print("Enter Contact Ref ID to delete: ");
        String referenceId = sc.nextLine().trim();

        Optional<Contact> contact = contactRepository.findByReferenceIdAndOwnerUserId(referenceId, ownerId);
        if (contact.isEmpty()) {
            System.out.println("Contact not found.");
            return;
        }

        if (!askYesNo(sc, "Are you sure you want to delete " + referenceId + "? [Y/N]: ")) {
            System.out.println("Delete cancelled.");
            return;
        }

        try {
            Contact deleted = contactRepository.deleteByReferenceIdAndOwnerUserId(referenceId, ownerId)
                    .orElseThrow(() -> new ContactDeleteException("Delete failed."));

            deletionNotifier.notifyDeleted(deleted);
            System.out.println("Delete successful.");
        } catch (ContactDeleteException | IllegalStateException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    private Contact copyForEdit(Contact original) {
        if (original instanceof PersonContact) {
            return new PersonContact((PersonContact) original);
        }
        if (original instanceof OrganizationContact) {
            return new OrganizationContact((OrganizationContact) original);
        }
        throw new IllegalArgumentException("Unsupported contact type.");
    }

    private boolean editDraft(Scanner sc, Contact draft) {
        while (true) {
            System.out.println("\nEdit Contact Draft [" + draft.getReferenceId() + "]");
            System.out.println("1. Edit Name");
            System.out.println("2. Replace Phone Numbers");
            System.out.println("3. Replace Email Addresses");
            System.out.println("4. Edit Address");
            System.out.println("5. Edit Notes");
            if (draft instanceof PersonContact) {
                System.out.println("6. Edit Relationship");
            } else {
                System.out.println("6. Edit Organization Type");
            }
            System.out.println("7. Save Changes");
            System.out.println("0. Cancel");
            System.out.print("Choose option: ");

            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        System.out.print("New Name: ");
                        draft.setName(sc.nextLine());
                        System.out.println("Name updated in draft.");
                        break;
                    case "2":
                        draft.setPhoneNumbers(readPhoneNumberList(sc));
                        System.out.println("Phone numbers updated in draft.");
                        break;
                    case "3":
                        draft.setEmailAddresses(readEmailAddressList(sc));
                        System.out.println("Email addresses updated in draft.");
                        break;
                    case "4":
                        System.out.print("New Address (optional): ");
                        draft.setAddress(sc.nextLine());
                        System.out.println("Address updated in draft.");
                        break;
                    case "5":
                        System.out.print("New Notes (optional): ");
                        draft.setNotes(sc.nextLine());
                        System.out.println("Notes updated in draft.");
                        break;
                    case "6":
                        if (draft instanceof PersonContact) {
                            System.out.print("New Relationship (optional): ");
                            ((PersonContact) draft).setRelationship(sc.nextLine());
                        } else {
                            System.out.print("New Organization Type (optional): ");
                            ((OrganizationContact) draft).setOrganizationType(sc.nextLine());
                        }
                        System.out.println("Type-specific field updated in draft.");
                        break;
                    case "7":
                        return true;
                    case "0":
                        return false;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private List<PhoneNumber> readPhoneNumberList(Scanner sc) {
        java.util.List<PhoneNumber> phoneNumbers = new java.util.ArrayList<>();
        while (true) {
            System.out.print("Phone Label (HOME/WORK/MOBILE/OTHER): ");
            String label = sc.nextLine();
            System.out.print("Phone Number: ");
            String number = sc.nextLine();
            phoneNumbers.add(new PhoneNumber(label, number));

            if (!askYesNo(sc, "Add another phone number? [Y/N]: ")) {
                return phoneNumbers;
            }
        }
    }

    private List<EmailAddress> readEmailAddressList(Scanner sc) {
        java.util.List<EmailAddress> emailAddresses = new java.util.ArrayList<>();
        while (true) {
            System.out.print("Email Label (PERSONAL/WORK/OTHER): ");
            String label = sc.nextLine();
            System.out.print("Email Address: ");
            String email = sc.nextLine();
            emailAddresses.add(new EmailAddress(label, email));

            if (!askYesNo(sc, "Add another email address? [Y/N]: ")) {
                return emailAddresses;
            }
        }
    }

    private boolean readPhoneNumbers(Scanner sc, Contact contact) {
        while (true) {
            System.out.print("Phone Label (HOME/WORK/MOBILE/OTHER): ");
            String label = sc.nextLine();

            System.out.print("Phone Number: ");
            String number = sc.nextLine();

            try {
                contact.addPhoneNumber(new PhoneNumber(label, number));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                continue;
            }

            if (!askYesNo(sc, "Add another phone number? [Y/N]: ")) {
                return true;
            }
        }
    }

    private boolean readEmailAddresses(Scanner sc, Contact contact) {
        while (true) {
            System.out.print("Email Label (PERSONAL/WORK/OTHER): ");
            String label = sc.nextLine();

            System.out.print("Email Address: ");
            String email = sc.nextLine();

            try {
                contact.addEmailAddress(new EmailAddress(label, email));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                continue;
            }

            if (!askYesNo(sc, "Add another email address? [Y/N]: ")) {
                return true;
            }
        }
    }

    private boolean askYesNo(Scanner sc, String prompt) {
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
