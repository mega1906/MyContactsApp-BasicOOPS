package com.mycontacts.search;

import com.mycontacts.contact.Contact;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

// Search by tags.
public class TagSearchCriteria implements SearchCriteria {
    private final Pattern pattern;

    public TagSearchCriteria(String query) {
        this.pattern = buildPattern(query);
    }

    @Override
    public boolean matches(Contact contact) {
        return contact.getTags().stream().anyMatch(tag -> pattern.matcher(tag).find());
    }

    private Pattern buildPattern(String query) {
        try {
            return Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            return Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);
        }
    }
}
