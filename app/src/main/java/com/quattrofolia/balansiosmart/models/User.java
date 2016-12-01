package com.quattrofolia.balansiosmart.models;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject implements AutoIncrementable {

    @PrimaryKey
    private int id;
    public RealmList<Goal> goals;
    public RealmList<HealthDataEntry> entries;

    @Override
    public void setPrimaryKey(int primaryKey) {
        this.id = primaryKey;
    }

    @Override
    public int getNextPrimaryKey(Realm realm) {
        return realm.where(User.class).max("id").intValue() + 1;
    }
}
