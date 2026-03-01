package com.mycontacts.search;

import com.mycontacts.contact.Contact;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

// Search by name (case-insensitive, regex supported).
public class NameSearchCriteria implements SearchCriteria {
    private final Pattern pattern;

    public NameSearchCriteria(String query) {
        this.pattern = buildPattern(query);
    }

    @Override
    public boolean matches(Contact contact) {
        return pattern.matcher(contact.getName()).find();
    }

    private Pattern buildPattern(String query) {
        try {
            return Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            return Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);
        }
    }
}
