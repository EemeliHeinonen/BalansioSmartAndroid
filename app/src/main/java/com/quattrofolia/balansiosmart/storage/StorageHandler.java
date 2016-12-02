package com.quattrofolia.balansiosmart.storage;

import io.realm.RealmObject;

public interface StorageHandler {
    void storageDataSaved(RealmObject savedObject);
    void storageDataError(Throwable error);
}
