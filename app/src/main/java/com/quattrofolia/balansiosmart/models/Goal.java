package com.quattrofolia.balansiosmart.models;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Goal extends RealmObject implements Incrementable {

    @PrimaryKey
    private int id;
    private String type;
    private Discipline discipline;
    private Range targetRange;

    public void setType(HealthDataType type) {
        this.type = type.toString();
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
