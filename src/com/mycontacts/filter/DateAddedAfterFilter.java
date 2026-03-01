package com.mycontacts.filter;

import com.mycontacts.contact.Contact;

import java.time.LocalDate;

// Filters contacts created on/after given date.
public class DateAddedAfterFilter implements ContactFilter {
    private final LocalDate date;

    public DateAddedAfterFilter(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean matches(Contact contact) {
        return !contact.getCreatedAt().toLocalDate().isBefore(date);
    }
}
