package com.mycontacts.tag;

import com.mycontacts.validation.Validators;

import java.util.Locale;
import java.util.Objects;

// Value object for contact tags.
public final class Tag {
    private final String name;
    private final String normalizedName;

    private Tag(String name) {
        if (!Validators.isValidTagName(name)) {
            throw new IllegalArgumentException("Tag must have 2 to 20 letters/spaces.");
        }
        String cleaned = name.trim();
        this.name = cleaned;
        this.normalizedName = cleaned.toUpperCase(Locale.ROOT);
    }

    public static Tag custom(String name) {
        return new Tag(name);
    }

    public static Tag predefined(PredefinedTag predefinedTag) {
        if (predefinedTag == null) {
            throw new IllegalArgumentException("Predefined tag cannot be null.");
        }
        return new Tag(predefinedTag.name());
    }

    public String getName() {
        return name;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        Tag tag = (Tag) o;
        return normalizedName.equals(tag.normalizedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(normalizedName);
    }
}
