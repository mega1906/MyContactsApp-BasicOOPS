package com.mycontacts.repository;

import com.mycontacts.tag.Tag;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

// In-memory tag storage per user.
public class TagRepository {
    private final HashMap<UUID, Set<Tag>> tagsByUser = new HashMap<>();

    public void saveForUser(UUID userId, Tag tag) {
        tagsByUser.computeIfAbsent(userId, key -> new LinkedHashSet<>()).add(tag);
    }

    public Set<Tag> findByUserId(UUID userId) {
        return Collections.unmodifiableSet(tagsByUser.getOrDefault(userId, Collections.emptySet()));
    }
}
