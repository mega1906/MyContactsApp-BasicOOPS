package com.mycontacts.repository;

import com.mycontacts.contact.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// In-memory repository for user contacts.
public class ContactRepository {
    private final Map<UUID, List<Contact>> contactsByUser = new HashMap<>();
    private final Map<UUID, Integer> sequenceByUser = new HashMap<>();

    public void save(Contact contact) {
        contactsByUser
                .computeIfAbsent(contact.getOwnerUserId(), key -> new ArrayList<>())
                .add(contact);
    }

    public List<Contact> findByOwnerUserId(UUID ownerUserId) {
        return Collections.unmodifiableList(
                contactsByUser.getOrDefault(ownerUserId, Collections.emptyList())
        );
    }

    public Optional<Contact> findByIdAndOwnerUserId(UUID contactId, UUID ownerUserId) {
        return contactsByUser.getOrDefault(ownerUserId, Collections.emptyList())
                .stream()
                .filter(contact -> contact.getId().equals(contactId))
                .findFirst();
    }

    public Optional<Contact> findByReferenceIdAndOwnerUserId(String referenceId, UUID ownerUserId) {
        if (referenceId == null || referenceId.isBlank()) {
            return Optional.empty();
        }
        String normalized = referenceId.trim().toUpperCase();
        return contactsByUser.getOrDefault(ownerUserId, Collections.emptyList())
                .stream()
                .filter(contact -> contact.getReferenceId().equals(normalized))
                .findFirst();
    }

    public String nextReferenceId(UUID ownerUserId, String ownerName) {
        int sequence = sequenceByUser.merge(ownerUserId, 1, Integer::sum);
        String prefix = buildPrefix(ownerName);
        return prefix + sequence;
    }

    // Replaces existing contact with same reference id for this owner.
    public boolean replaceByReferenceIdAndOwnerUserId(String referenceId, UUID ownerUserId, Contact replacement) {
        if (referenceId == null || ownerUserId == null || replacement == null) {
            return false;
        }
        String normalized = referenceId.trim().toUpperCase();
        List<Contact> contacts = contactsByUser.getOrDefault(ownerUserId, Collections.emptyList());
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getReferenceId().equals(normalized)) {
                contacts.set(i, replacement);
                return true;
            }
        }
        return false;
    }

    public Optional<Contact> deleteByReferenceIdAndOwnerUserId(String referenceId, UUID ownerUserId) {
        if (referenceId == null || referenceId.isBlank()) {
            return Optional.empty();
        }
        String normalized = referenceId.trim().toUpperCase();
        List<Contact> contacts = contactsByUser.getOrDefault(ownerUserId, Collections.emptyList());
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            if (contact.getReferenceId().equals(normalized)) {
                contact.cascadeDeleteRelatedData();
                contacts.remove(i);
                return Optional.of(contact);
            }
        }
        return Optional.empty();
    }

    private String buildPrefix(String ownerName) {
        if (ownerName == null || ownerName.isBlank()) {
            return "USR";
        }
        String letters = ownerName.replaceAll("[^A-Za-z]", "").toUpperCase();
        if (letters.isEmpty()) {
            return "USR";
        }
        return letters.length() >= 3 ? letters.substring(0, 3) : letters;
    }
}
