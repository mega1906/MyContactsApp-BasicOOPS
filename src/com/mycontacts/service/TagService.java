package com.mycontacts.service;

import com.mycontacts.auth.AuthSession;
import com.mycontacts.auth.SessionManager;
import com.mycontacts.contact.Contact;
import com.mycontacts.repository.ContactRepository;
import com.mycontacts.repository.TagRepository;
import com.mycontacts.tag.ContactTagAssociation;
import com.mycontacts.tag.PredefinedTag;
import com.mycontacts.tag.Tag;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

// Handles UC-11: create and manage tags.
public class TagService {
    private final TagRepository tagRepository;
    private final ContactRepository contactRepository;
    private final SessionManager sessionManager;

    public TagService(TagRepository tagRepository, ContactRepository contactRepository, SessionManager sessionManager) {
        this.tagRepository = tagRepository;
        this.contactRepository = contactRepository;
        this.sessionManager = sessionManager;
    }

    public void openTagMenu(Scanner sc) {
        Optional<AuthSession> current = sessionManager.getCurrentSession();
        if (current.isEmpty()) {
            System.out.println("No active session.");
            return;
        }

        UUID userId = current.get().getUser().getId();
        while (true) {
            System.out.println("\nManage Tags");
            System.out.println("1. Create Custom Tag");
            System.out.println("2. Show Available Tags");
            System.out.println("3. Apply Tag(s) To Contact");
            System.out.println("4. Remove Tag From Contact");
            System.out.println("5. Show Contacts By Tag");
            System.out.println("0. Back");
            System.out.print("Choose option: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    createCustomTag(sc, userId);
                    break;
                case "2":
                    showAllTags(userId);
                    break;
                case "3":
                    applyTagToContact(sc, userId);
                    break;
                case "4":
                    removeTagFromContact(sc, userId);
                    break;
                case "5":
                    showContactsByTag(sc, userId);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public Tag saveOrCreateTag(UUID userId, String rawTagName) {
        Tag tag = Tag.custom(rawTagName);
        tagRepository.saveForUser(userId, tag);
        return tag;
    }

    public Set<Tag> getAllTags(UUID userId) {
        Set<Tag> tags = EnumSet.allOf(PredefinedTag.class).stream()
                .map(Tag::predefined)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        tags.addAll(tagRepository.findByUserId(userId));
        return tags;
    }

    private void createCustomTag(Scanner sc, UUID userId) {
        System.out.print("Custom tag name: ");
        String name = sc.nextLine();
        try {
            Tag tag = saveOrCreateTag(userId, name);
            System.out.println("Tag saved: " + tag.getName());
        } catch (IllegalArgumentException e) {
            System.out.println("Unable to create tag: " + e.getMessage());
        }
    }

    private void showAllTags(UUID userId) {
        String names = getAllTags(userId).stream()
                .map(Tag::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining(", "));
        System.out.println("Available tags: " + (names.isBlank() ? "N/A" : names));
    }

    private void applyTagToContact(Scanner sc, UUID userId) {
        Optional<Contact> contact = findContactByRef(sc, userId, "Enter Contact Ref ID: ");
        if (contact.isEmpty()) {
            return;
        }

        showAllTags(userId);
        System.out.print("Tag(s) to apply (comma separated): ");
        String tagInput = sc.nextLine();
        try {
            Set<ContactTagAssociation> links = parseAssociations(
                    userId,
                    contact.get().getReferenceId(),
                    tagInput
            );
            int applied = applyAssociations(contact.get(), links);
            System.out.println("Tag apply done. Added: " + applied);
        } catch (IllegalArgumentException e) {
            System.out.println("Unable to apply tag: " + e.getMessage());
        }
    }

    private void removeTagFromContact(Scanner sc, UUID userId) {
        Optional<Contact> contact = findContactByRef(sc, userId, "Enter Contact Ref ID: ");
        if (contact.isEmpty()) {
            return;
        }
        if (contact.get().getTags().isEmpty()) {
            System.out.println("This contact has no tags.");
            return;
        }

        String existing = contact.get().getTags().stream()
                .map(Tag::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining(", "));
        System.out.println("Current tags: " + existing);
        System.out.print("Tag to remove: ");
        String name = sc.nextLine();
        try {
            Tag tag = Tag.custom(name);
            boolean removed = contact.get().removeTag(tag);
            if (removed) {
                tagRepository.unlinkTagFromContact(userId, tag, contact.get().getReferenceId());
            }
            System.out.println(removed ? "Tag removed." : "Tag not found on this contact.");
        } catch (IllegalArgumentException e) {
            System.out.println("Unable to remove tag: " + e.getMessage());
        }
    }

    private void showContactsByTag(Scanner sc, UUID userId) {
        showAllTags(userId);
        System.out.print("Tag name: ");
        String name = sc.nextLine();
        try {
            Tag tag = Tag.custom(name);
            Set<String> refs = tagRepository.findContactRefsByTag(userId, tag);
            if (refs.isEmpty()) {
                System.out.println("No contacts mapped to this tag.");
                return;
            }
            System.out.println("Contacts with " + tag.getName() + ": " + String.join(", ", refs));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid tag: " + e.getMessage());
        }
    }

    private Set<ContactTagAssociation> parseAssociations(UUID userId, String refId, String tagInput) {
        Set<ContactTagAssociation> links = Arrays.stream(tagInput.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(Tag::custom)
                .map(tag -> new ContactTagAssociation(userId, refId, tag))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (links.isEmpty()) {
            throw new IllegalArgumentException("Please enter at least one valid tag.");
        }
        return links;
    }

    // Update both sides: contact -> tags and tag -> contacts.
    private int applyAssociations(Contact contact, Set<ContactTagAssociation> links) {
        int added = 0;
        for (ContactTagAssociation link : links) {
            saveOrCreateTag(link.getOwnerUserId(), link.getTag().getName());
            int before = contact.getTags().size();
            contact.addTag(link.getTag());
            if (contact.getTags().size() > before) {
                added++;
            }
            tagRepository.linkTagToContact(link.getOwnerUserId(), link.getTag(), link.getContactReferenceId());
        }
        return added;
    }

    private Optional<Contact> findContactByRef(Scanner sc, UUID userId, String prompt) {
        List<Contact> contacts = contactRepository.findByOwnerUserId(userId);
        if (contacts.isEmpty()) {
            System.out.println("No contacts found for this user.");
            return Optional.empty();
        }

        String refs = contacts.stream()
                .map(Contact::getReferenceId)
                .sorted()
                .collect(Collectors.joining(", "));
        System.out.println("Available Contact Ref IDs: " + refs);
        System.out.print(prompt);
        String referenceId = sc.nextLine().trim();
        Optional<Contact> contact = contactRepository.findByReferenceIdAndOwnerUserId(referenceId, userId);
        if (contact.isEmpty()) {
            System.out.println("Contact not found.");
        }
        return contact;
    }
}
