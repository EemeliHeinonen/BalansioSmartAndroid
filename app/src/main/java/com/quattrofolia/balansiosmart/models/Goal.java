package com.quattrofolia.balansiosmart.models;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Goal extends RealmObject implements Incrementable {

    /* Each Goal object is stored in a User object's goals
     * RealmList. A goal defines a set of rules for a single
     * type of health-related data. The assistive interface
     * of the application is driven by these rules. */

    @PrimaryKey
    private int id;
    @Required
    private String type;
    private String notificationStyle;


    private String notificationIntensity;
    private Discipline discipline;
    private Range targetRange;

    public void setType(HealthDataType type) {
        this.type = type.toString();
    }

    public void setNotificationStyle(String notificationStyle) {
        this.notificationStyle = notificationStyle;
    }

    /* Use enums for restricted set of string parameters */
    public void setNotificationIntensity(NotificationIntensity intensity) {
        this.notificationIntensity = intensity.toString();
    }

    public NotificationIntensity getNotificationIntensity() {
        return NotificationIntensity.valueOf(this.notificationIntensity);
    }

    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
    }

    public void setTargetRange(Range targetRange) {
        this.targetRange = targetRange;
    }

    public HealthDataType getType() {
        return HealthDataType.valueOf(type);
    }

    public Discipline getDiscipline() {
        return this.discipline;
    }

    public Range getTargetRange() {
        return targetRange;
    }

    public String getNotificationStyle() {
        return notificationStyle;
    }

    public int getId() {
        return id;
    }

    @Override
    public int getNextPrimaryKey(Realm realm) {
        Number n = realm.where(Goal.class).max("id");
        if (n != null) {
            return n.intValue() + 1;
        } else {
            return 0;
        }
    }

    @Override
    public void setPrimaryKey(int id) {
        this.id = id;
    }
}
