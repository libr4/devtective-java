package com.devtective.devtective.common.dto;

import java.util.Objects;

public record Name(String firstName, String lastName) {
    public static Name parseFullName(String fullName) {
        String treatedFullName = Objects.toString(fullName, "").trim();
        String firstName = null;
        String lastName  = null;
        if (!treatedFullName.isEmpty()) {
            String[] parts = fullName.split("\\s+", 2); // split into [first, rest]
            firstName = parts[0];
            lastName  = (parts.length == 2) ? parts[1] : null;
        }
        return new Name(firstName, lastName);
    }
    public String getDisplayName() {
        return (firstName + " " + lastName).trim();
    }
}
