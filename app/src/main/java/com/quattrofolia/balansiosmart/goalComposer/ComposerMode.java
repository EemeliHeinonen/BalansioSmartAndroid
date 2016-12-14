package com.quattrofolia.balansiosmart.goalComposer;

/* Use for string identifiers for intents to launch Goal Composer.
* intent.*/
public enum ComposerMode {

    CREATE ("CREATE"),
    EDIT ("EDIT"),
    GENERATE ("GENERATE");

    public final String identifier;

    ComposerMode(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return this.identifier;
    }
}
