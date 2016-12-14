package com.quattrofolia.balansiosmart.models;

public enum NotificationIntensity {
    STRICT("STRICT"),
    EASY("EASY"),
    NONE("NONE");

    private final String identifier;

    NotificationIntensity(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return this.identifier;
    }
}
