package com.devtective.devtective.dominio.user;

public enum RoleConstants {
    USER("USER"),
    ADMIN("ADMIN");

    private final String displayName;

    RoleConstants(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }   
}
