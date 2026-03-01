package com.mycontacts.service;

import com.mycontacts.auth.AuthSession;
import com.mycontacts.auth.SessionManager;
import com.mycontacts.contact.Contact;
import com.mycontacts.contact.EmailAddress;
import com.mycontacts.contact.OrganizationContact;
import com.mycontacts.contact.PersonContact;
import com.mycontacts.contact.PhoneNumber;
import com.mycontacts.repository.ContactRepository;
import com.mycontacts.user.User;

import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

// Handles UC-04 contact creation.
public class ContactService {
    private final ContactRepository contactRepository;
    private final SessionManager sessionManager;

    public ContactService(ContactRepository contactRepository, SessionManager sessionManager) {
        this.contactRepository = contactRepository;
        this.sessionManager = sessionManager;
    }

    public void createContact(Scanner sc) {
        Optional<AuthSession> current = sessionManager.getCurrentSession();
        if (current.isEmpty()) {
            System.out.println("No active session.");
            return;
        }

        User owner = current.get().getUser();
        UUID ownerId = owner.getId();

        System.out.println("\nCreate Contact");
        System.out.print("Contact Type [PERSON/ORGANIZATION]: ");
        String type = sc.nextLine().trim().toUpperCase();

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        Contact contact;
        try {
            if ("PERSON".equals(type)) {
                PersonContact person = new PersonContact(ownerId, name);
                System.out.print("Relationship (optional): ");
                person.setRelationship(sc.nextLine());
                contact = person;
            } else if ("ORGANIZATION".equals(type)) {
                OrganizationContact organization = new OrganizationContact(ownerId, name);
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
        System.out.println("Contact ID: " + contact.getId());
        System.out.println("Created At: " + contact.getCreatedAt());
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
