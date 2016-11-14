package com.quattrofolia.balansiosmart.storage;

import android.util.Log;

import com.quattrofolia.balansiosmart.models.AutoIncrementable;

import io.realm.Realm;
import io.realm.RealmObject;

public class Storage {
    Realm realm;
    private static final String TAG = "Storage";

    public Storage() {
        try {
            realm = realm.getDefaultInstance();
        } catch (Exception e) {
            Log.e("Realm init failed", e.getMessage());
        }
    }

    protected void save(final RealmObject object) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                if (object instanceof AutoIncrementable) {
                    AutoIncrementable autoIncrementable = (AutoIncrementable) object;
                    autoIncrementable.setPrimaryKey(autoIncrementable.getNextPrimaryKey(bgRealm));
                    bgRealm.copyToRealm((RealmObject) autoIncrementable);
                } else {
                    bgRealm.copyToRealm(object);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // success
                Log.d(TAG, "saved: " + object.toString());
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // error
                Log.e("error saving " + object.toString(), error.getMessage());
            }
        });
    }
}
