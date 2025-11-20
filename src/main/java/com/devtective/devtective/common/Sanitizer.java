package com.devtective.devtective.common;

public class Sanitizer {

    public static String sanitizeString(String input) {
        if (input == null) {
            return "";
        }
        return input.trim();
    }
    
}
