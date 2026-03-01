package com.mycontacts.search;

import com.mycontacts.contact.Contact;

import java.util.List;

// Combines multiple criteria into one query (AND).
public class AndSearchCriteria implements SearchCriteria {
    private final List<SearchCriteria> criteria;

    public AndSearchCriteria(List<SearchCriteria> criteria) {
        this.criteria = List.copyOf(criteria);
    }

    @Override
    public boolean matches(Contact contact) {
        return criteria.stream().allMatch(c -> c.matches(contact));
    }
}
