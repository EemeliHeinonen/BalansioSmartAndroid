package com.quattrofolia.balansiosmart;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class BalansioSmart extends Application {
    private final static String TAG = "BalansioSmart";
    public static int userId;
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
