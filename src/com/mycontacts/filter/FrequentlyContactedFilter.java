package com.mycontacts.filter;

import com.mycontacts.contact.Contact;

// Filters contacts by minimum contacted count.
public class FrequentlyContactedFilter implements ContactFilter {
    private final int minimumCount;

    public FrequentlyContactedFilter(int minimumCount) {
        this.minimumCount = minimumCount;
    }

    @Override
    public boolean matches(Contact contact) {
        return contact.getContactedCount() >= minimumCount;
    }
}
