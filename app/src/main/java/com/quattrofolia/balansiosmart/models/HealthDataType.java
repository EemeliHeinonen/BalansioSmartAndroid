package com.quattrofolia.balansiosmart.models;

/* This enum defines the supported health data types.
* All values are assigned with a Unit object that
* can be accessed via unit() method. */

public enum HealthDataType {

    BLOOD_GLUCOSE("Blood Glucose", "bg", Unit.MMOL_PER_L),
    WEIGHT("Weight", "wt", Unit.KG),
    BLOOD_PRESSURE_SYSTOLIC("Blood Pressure Systolic", "bps", Unit.MMHG),
    BLOOD_PRESSURE_DIASTOLIC("Blood Pressure Diastolic", "bpd", Unit.MMHG),
    SLEEP("Sleep", "slp", Unit.HOUR),
    EXERCISE("Exercise", "xrc", Unit.MINUTE),
    NUTRITION("Nutrition", "ntr", Unit.KCAL);

    private final String longName;
    private final String shortName;
    private final Unit unit;

    HealthDataType(String longName, String shortName, Unit unit) {
        this.longName = longName;
        this.shortName = shortName;
        this.unit = unit;
    }

    public String getLongName() { return longName; }
    public String getShortName() { return shortName; }
    public Unit getUnit() {
        return unit;
    }
}
