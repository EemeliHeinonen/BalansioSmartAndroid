package com.quattrofolia.balansiosmart.models;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Goal extends RealmObject implements AutoIncrementable {

    @PrimaryKey
    private int id;
    private String type;
    private String notificationStyle;
    private Discipline discipline;
    private Range targetRange;

    public void setType(HealthDataType type) {
        this.type = type.toString();
    }

    public void setNotificationStyle(String notificationStyle){
        this.notificationStyle = notificationStyle;
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
        return discipline;
    }

    public Range getTargetRange() {
        return targetRange;
    }

    public String getNotificationStyle(){
        return notificationStyle;
    }

    @Override
    public void setPrimaryKey(int primaryKey) {
        this.id = primaryKey;
    }

    @Override
    public int getNextPrimaryKey(Realm realm) {
        return realm.where(Goal.class).max("id").intValue() + 1;
    }
}
