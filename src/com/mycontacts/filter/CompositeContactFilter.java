package com.mycontacts.filter;

import com.mycontacts.contact.Contact;

import java.util.List;

// Combines many filters into one (AND logic).
public class CompositeContactFilter implements ContactFilter {
    private final List<ContactFilter> filters;

    public CompositeContactFilter(List<ContactFilter> filters) {
        this.filters = List.copyOf(filters);
    }

    @Override
    public boolean matches(Contact contact) {
        return filters.stream().allMatch(filter -> filter.matches(contact));
    }
}
