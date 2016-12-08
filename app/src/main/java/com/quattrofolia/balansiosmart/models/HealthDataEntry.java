package com.quattrofolia.balansiosmart.models;

import org.joda.time.Instant;

import java.math.BigDecimal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class HealthDataEntry extends RealmObject implements Incrementable {

    @PrimaryKey
    private int id;
    @Required
    private String type;
    @Required
    private String value;
    private long instant;

    public HealthDataEntry() {}
    public HealthDataEntry(String type, String value, Instant instant) {
        this.type = type;
        this.value = value;
        this.instant = instant.getMillis();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(HealthDataType type) {
        this.type = type.toString();
    }

    public void setValue(BigDecimal value) {
        this.value = value.toString();
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

    public Instant getInstant() {
        return new Instant(instant);
    }

    @Override
    public int getNextPrimaryKey(Realm realm) {
        Number n = realm.where(HealthDataEntry.class).max("id");
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