package com.mycontacts.repository;

import com.mycontacts.tag.Tag;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// In-memory tag storage per user.
public class TagRepository {
    private final HashMap<UUID, Set<Tag>> tagsByUser = new HashMap<>();
    private final HashMap<UUID, Map<Tag, Set<String>>> contactsByTagByUser = new HashMap<>();

    public void saveForUser(UUID userId, Tag tag) {
        tagsByUser.computeIfAbsent(userId, key -> new LinkedHashSet<>()).add(tag);
    }

    public Set<Tag> findByUserId(UUID userId) {
        return Collections.unmodifiableSet(tagsByUser.getOrDefault(userId, Collections.emptySet()));
    }

    // Reverse side: which contacts are linked to a tag.
    public void linkTagToContact(UUID userId, Tag tag, String contactReferenceId) {
        contactsByTagByUser
                .computeIfAbsent(userId, key -> new HashMap<>())
                .computeIfAbsent(tag, key -> new LinkedHashSet<>())
                .add(contactReferenceId.trim().toUpperCase());
    }

    public void unlinkTagFromContact(UUID userId, Tag tag, String contactReferenceId) {
        Map<Tag, Set<String>> byTag = contactsByTagByUser.get(userId);
        if (byTag == null) {
            return;
        }
        Set<String> refs = byTag.get(tag);
        if (refs == null) {
            return;
        }
        refs.remove(contactReferenceId.trim().toUpperCase());
        if (refs.isEmpty()) {
            byTag.remove(tag);
        }
    }

    public Set<String> findContactRefsByTag(UUID userId, Tag tag) {
        Map<Tag, Set<String>> byTag = contactsByTagByUser.getOrDefault(userId, Collections.emptyMap());
        Set<String> refs = byTag.getOrDefault(tag, Collections.emptySet());
        return Collections.unmodifiableSet(refs);
    }
}
