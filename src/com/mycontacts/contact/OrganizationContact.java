package com.mycontacts.contact;

import java.util.UUID;

// Concrete contact for an organization.
public class OrganizationContact extends Contact {
    private String organizationType;

    public OrganizationContact(UUID ownerUserId, String referenceId, String name) {
        super(ownerUserId, referenceId, name);
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = (organizationType == null || organizationType.isBlank()) ? null : organizationType.trim();
        touch();
    }

    @Override
    public String getContactType() {
        return "ORGANIZATION";
    }
}
