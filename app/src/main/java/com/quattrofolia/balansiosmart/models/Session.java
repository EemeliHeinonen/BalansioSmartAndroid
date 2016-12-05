package com.quattrofolia.balansiosmart.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class Session extends RealmObject {

    @PrimaryKey
    @Required
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

}
