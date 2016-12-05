package com.quattrofolia.balansiosmart.models;

import io.realm.Realm;

public interface Incrementable {
    public int getNextPrimaryKey(Realm realm);
    public void setPrimaryKey(int id);
}
