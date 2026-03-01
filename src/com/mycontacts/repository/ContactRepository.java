package com.mycontacts.repository;

import com.mycontacts.contact.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// In-memory repository for user contacts.
public class ContactRepository {
    private final Map<UUID, List<Contact>> contactsByUser = new HashMap<>();

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
}
