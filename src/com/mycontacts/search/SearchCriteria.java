package com.mycontacts.search;

import com.mycontacts.contact.Contact;

import java.util.function.Predicate;

// Contract for any contact search rule.
public interface SearchCriteria {
    boolean matches(Contact contact);

    default Predicate<Contact> toPredicate() {
        return this::matches;
    }
}
