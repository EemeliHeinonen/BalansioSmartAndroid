package com.quattrofolia.balansiosmart;

import android.app.Application;

import com.quattrofolia.balansiosmart.models.Session;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class BalansioSmart extends Application {
    private final static String TAG = "BalansioSmart";
    private Realm realm;
    public static Session session;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        session = currentSession(realm);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        realm.close();
    }

    public static Session currentSession(Realm realm) {

        /* Return the latest sesssion from database */

        RealmResults<Session> sessions = realm.where(Session.class).findAll();
        if (!sessions.isEmpty()) {
            return sessions.last();
        } else {
            return null;
        }
    }
}