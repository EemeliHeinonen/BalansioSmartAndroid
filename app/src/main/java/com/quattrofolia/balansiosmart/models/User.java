package com.quattrofolia.balansiosmart.models;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject implements Incrementable {

    @PrimaryKey
    private int id;
    private String firstName;
    private String lastName;
    private RealmList<Goal> goals;
    private RealmList<HealthDataEntry> entries;

    public User() {
        this.goals = new RealmList<>();
    }

    public User(String firstName, String lastName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public RealmList<Goal> getGoals() {
        return goals;
    }

    public void setGoals(RealmList<Goal> goals) {
        this.goals = goals;
    }

    public RealmList<HealthDataEntry> getEntries() {
        return entries;
    }

    public void setEntries(RealmList<HealthDataEntry> entries) {
        this.entries = entries;
    }

    @Override
    public int getNextPrimaryKey(Realm realm) {
        Number n = realm.where(User.class).max("id");
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
