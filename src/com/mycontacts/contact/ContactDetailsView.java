package com.mycontacts.contact;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Immutable view object for contact details display.
public final class ContactDetailsView {
    private final UUID id;
    private final String referenceId;
    private final String type;
    private final String name;
    private final List<String> phones;
    private final List<String> emails;
    private final List<String> tags;
    private final int contactedCount;
    private final Optional<String> address;
    private final Optional<String> notes;
    private final Optional<String> relationship;
    private final Optional<String> organizationType;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private ContactDetailsView(
            UUID id,
            String referenceId,
            String type,
            String name,
            List<String> phones,
            List<String> emails,
            List<String> tags,
            int contactedCount,
            Optional<String> address,
            Optional<String> notes,
            Optional<String> relationship,
            Optional<String> organizationType,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.referenceId = referenceId;
        this.type = type;
        this.name = name;
        this.phones = List.copyOf(phones);
        this.emails = List.copyOf(emails);
        this.tags = List.copyOf(tags);
        this.contactedCount = contactedCount;
        this.address = address;
        this.notes = notes;
        this.relationship = relationship;
        this.organizationType = organizationType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ContactDetailsView from(Contact contact) {
        Optional<String> relationship = Optional.empty();
        Optional<String> organizationType = Optional.empty();

        if (contact instanceof PersonContact) {
            relationship = Optional.ofNullable(((PersonContact) contact).getRelationship())
                    .filter(value -> !value.isBlank());
        } else if (contact instanceof OrganizationContact) {
            organizationType = Optional.ofNullable(((OrganizationContact) contact).getOrganizationType())
                    .filter(value -> !value.isBlank());
        }

        return new ContactDetailsView(
                contact.getId(),
                contact.getReferenceId(),
                contact.getContactType(),
                contact.getName(),
                contact.getPhoneNumbers().stream().map(PhoneNumber::toString).toList(),
                contact.getEmailAddresses().stream().map(EmailAddress::toString).toList(),
                contact.getTags().stream().sorted().toList(),
                contact.getContactedCount(),
                Optional.ofNullable(contact.getAddress()).filter(value -> !value.isBlank()),
                Optional.ofNullable(contact.getNotes()).filter(value -> !value.isBlank()),
                relationship,
                organizationType,
                contact.getCreatedAt(),
                contact.getUpdatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public String getName() {
        return name;
    }

    public List<String> getPhones() {
        return phones;
    }

    public List<String> getEmails() {
        return emails;
    }

    public List<String> getTags() {
        return tags;
    }

    public int getContactedCount() {
        return contactedCount;
    }

    public Optional<String> getAddress() {
        return address;
    }

    public Optional<String> getNotes() {
        return notes;
    }

    public Optional<String> getRelationship() {
        return relationship;
    }

    public Optional<String> getOrganizationType() {
        return organizationType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return String.format(
                "Contact Details%n" +
                        "Ref ID: %s%n" +
                        "System ID: %s%n" +
                        "Type: %s%n" +
                        "Name: %s%n" +
                        "Phones: %s%n" +
                        "Emails: %s%n" +
                        "Tags: %s%n" +
                        "Contacted Count: %d%n" +
                        "Address: %s%n" +
                        "Notes: %s%n" +
                        "Relationship: %s%n" +
                        "Organization Type: %s%n" +
                        "Created At: %s%n" +
                        "Updated At: %s",
                referenceId,
                id,
                type,
                name,
                phones,
                emails,
                tags,
                contactedCount,
                address.orElse("N/A"),
                notes.orElse("N/A"),
                relationship.orElse("N/A"),
                organizationType.orElse("N/A"),
                createdAt,
                updatedAt
        );
    }
}
