package com.mycontacts.search;

import com.mycontacts.contact.Contact;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

// Search by any phone number.
public class PhoneSearchCriteria implements SearchCriteria {
    private final Pattern pattern;

    public PhoneSearchCriteria(String query) {
        this.pattern = buildPattern(query);
    }

    @Override
    public boolean matches(Contact contact) {
        return contact.getPhoneNumbers().stream()
                .map(phone -> phone.getNumber())
                .anyMatch(number -> pattern.matcher(number).find());
    }

    private Pattern buildPattern(String query) {
        try {
            return Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            return Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);
        }
    }
}
