package com.quattrofolia.balansiosmart.models;

import io.realm.Realm;

/* Use this interface on objects with int primary keys
* that need to be incremented upon saving to database. */

public interface Incrementable {
    public int getNextPrimaryKey(Realm realm);
    public void setPrimaryKey(int id);
}
