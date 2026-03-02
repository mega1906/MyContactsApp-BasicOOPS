package com.mycontacts.filter;

import com.mycontacts.contact.Contact;
import com.mycontacts.tag.Tag;

// Filters contacts by tag (case-insensitive via normalized tags).
public class TagContactFilter implements ContactFilter {
    private final Tag tag;

    public TagContactFilter(String tag) {
        this.tag = Tag.custom(tag);
    }

    @Override
    public boolean matches(Contact contact) {
        return contact.getTags().contains(tag);
    }
}
