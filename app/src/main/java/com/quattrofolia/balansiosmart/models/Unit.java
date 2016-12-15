package com.quattrofolia.balansiosmart.models;

/* This enum defines the units for the supported
* health data types. Each unit is assigned with
* a human-readable string that can be accessed
* via toString() method. */

public enum Unit {
    KG("kg"),
    MMOL_PER_L("mmol/l"),
    MMHG("mmHg"),
    MINUTE("min"),
    HOUR("h"),
    KCAL("kCal");


    private final String str;

    private Unit(final String s) {
        this.str = s;
    }
    @Override
    public String toString() {
        return str;
    }
}
