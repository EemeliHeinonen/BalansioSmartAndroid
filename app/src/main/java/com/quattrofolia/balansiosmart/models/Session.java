package com.quattrofolia.balansiosmart.models;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Session extends RealmObject implements Incrementable {

    /* Session object persists the user id between sessions when
    * the application is actually running. */

    @PrimaryKey
    int id;

    private Integer userId;

    public Session() {
    }

    public Session(int userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public int getNextPrimaryKey(Realm realm) {
        Number n = realm.where(Session.class).max("id");
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
