package com.quattrofolia.balansiosmart;

import android.app.Application;

import com.quattrofolia.balansiosmart.models.User;
import com.quattrofolia.balansiosmart.storage.Storage;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class BalansioSmart extends Application {
    private final static String TAG = "BalansioSmart";
    private Realm realm;
    private Storage storage;
    public static Integer userId;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        storage = new Storage();
        userId = loggedUser();
    }

    public Integer loggedUser() {

        /* Check if user is logged in
        * Use a mock user object for now. */

        RealmResults<User> userResults = realm.where(User.class).findAll();
        if (!userResults.isEmpty()) {
            return userResults.last().getId();
        } else {
            return null;
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        realm.close();
    }
}