package com.mycontacts.filter;

import com.mycontacts.contact.Contact;

// Filters contacts by tag (case-insensitive via normalized tags).
public class TagContactFilter implements ContactFilter {
    private final String tag;

    public TagContactFilter(String tag) {
        this.tag = tag.trim().toUpperCase();
    }

    @Override
    public boolean matches(Contact contact) {
        return contact.getTags().contains(tag);
    }
}
