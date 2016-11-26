package com.quattrofolia.balansiosmart.storage;

import android.util.Log;

import com.quattrofolia.balansiosmart.models.Incrementable;

import io.realm.Realm;
import io.realm.RealmObject;

public class Storage implements StorageHandler {
    private Realm realm;
    private static final String TAG = "Storage";

    public Storage() {
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    // Use this method for persisting RealmObjects
    public void save(final RealmObject object) {
        realm.executeTransactionAsync(new Realm.Transaction() {

            @Override
            public void execute(Realm bgRealm) {
                if (object instanceof Incrementable) {
                    // Increment primary key for autoincrementable RealmObjects
                    Incrementable incrementableObject = (Incrementable) object;
                    incrementableObject.setPrimaryKey(incrementableObject.getNextPrimaryKey(bgRealm));
                    bgRealm.copyToRealmOrUpdate((RealmObject) incrementableObject);
                } else {
                    bgRealm.copyToRealmOrUpdate(object);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                storageDataSaved(object);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
                storageDataError(error);
            }
        });
    }

    public void storageDataSaved(RealmObject savedObject) {
        Log.d(TAG, "saved.");
    }

    public void storageDataError(Throwable error) {
        Log.d(TAG, "error.");
    }
}
