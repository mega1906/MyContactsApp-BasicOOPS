package com.mycontacts.search;

import com.mycontacts.contact.Contact;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

// Search by any email address.
public class EmailSearchCriteria implements SearchCriteria {
    private final Pattern pattern;

    public EmailSearchCriteria(String query) {
        this.pattern = buildPattern(query);
    }

    @Override
    public boolean matches(Contact contact) {
        return contact.getEmailAddresses().stream()
                .map(email -> email.getEmail())
                .anyMatch(value -> pattern.matcher(value).find());
    }

    private Pattern buildPattern(String query) {
        try {
            return Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            return Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);
        }
    }
}
