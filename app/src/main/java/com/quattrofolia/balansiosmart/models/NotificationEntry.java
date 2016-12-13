package com.quattrofolia.balansiosmart.models;

import org.joda.time.Instant;

import java.math.BigDecimal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NotificationEntry extends RealmObject implements Incrementable {

    @PrimaryKey
    private int id;
    private String type;
    private String notificationText;

    public NotificationEntry() {}
    public NotificationEntry(String type, String value, Instant instant, String notificationText) {
        this.type = type;
        this.value = value;
        this.instant = instant.getMillis();
        this.notificationText = notificationText;
    }

    private String value;
    private long instant;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(HealthDataType type) {
        this.type = type.toString();
    }

    public void setValue(String value) {
        this.value = value.toString();
    }

    public void setNotificationText(String notificationText){
        this.notificationText = notificationText.toString();
    }

    public void setInstant(Instant instant) {
        this.instant = instant.getMillis();
    }

    public HealthDataType getType() {
        return HealthDataType.valueOf(type);
    }

    public String getValue() {
        return value;
    }

    public String getNotificationText(){
        return notificationText;
    }

    public Instant getInstant() {
        return new Instant(instant);
    }

    @Override
    public int getNextPrimaryKey(Realm realm) {
        Number n = realm.where(NotificationEntry.class).max("id");
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