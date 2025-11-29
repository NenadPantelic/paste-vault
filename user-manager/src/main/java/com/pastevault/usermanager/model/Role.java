package com.pastevault.usermanager.model;

public enum Role {

    ADMIN,
    USER;

    public static Role fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unable to map null value to a valid Role instance.");
        }

        for (Role role : values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }

        throw new IllegalArgumentException(String.format("Unable to map %s value to a valid Role instance.", value));
    }
}
