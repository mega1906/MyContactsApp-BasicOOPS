package com.mycontacts.tag;

import java.util.Objects;
import java.util.UUID;

// Simple association object between one contact and one tag.
public final class ContactTagAssociation {
    private final UUID ownerUserId;
    private final String contactReferenceId;
    private final Tag tag;

    public ContactTagAssociation(UUID ownerUserId, String contactReferenceId, Tag tag) {
        if (ownerUserId == null) {
            throw new IllegalArgumentException("Owner id cannot be null.");
        }
        if (contactReferenceId == null || contactReferenceId.isBlank()) {
            throw new IllegalArgumentException("Contact ref id cannot be blank.");
        }
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be null.");
        }
        this.ownerUserId = ownerUserId;
        this.contactReferenceId = contactReferenceId.trim().toUpperCase();
        this.tag = tag;
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public String getContactReferenceId() {
        return contactReferenceId;
    }

    public Tag getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContactTagAssociation)) {
            return false;
        }
        ContactTagAssociation that = (ContactTagAssociation) o;
        return ownerUserId.equals(that.ownerUserId)
                && contactReferenceId.equals(that.contactReferenceId)
                && tag.equals(that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerUserId, contactReferenceId, tag);
    }
}
