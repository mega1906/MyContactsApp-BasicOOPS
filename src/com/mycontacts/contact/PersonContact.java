package com.mycontacts.contact;

import java.util.UUID;

// Concrete contact for a person.
public class PersonContact extends Contact {
    private String relationship;

    public PersonContact(UUID ownerUserId, String referenceId, String name) {
        super(ownerUserId, referenceId, name);
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = (relationship == null || relationship.isBlank()) ? null : relationship.trim();
        touch();
    }

    @Override
    public String getContactType() {
        return "PERSON";
    }
}
