package com.mycontacts.filter;

import com.mycontacts.contact.Contact;

import java.util.function.Predicate;

// Base contract for advanced filtering rules.
public interface ContactFilter {
    boolean matches(Contact contact);

    default Predicate<Contact> toPredicate() {
        return this::matches;
    }
}
