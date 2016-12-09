package com.quattrofolia.balansiosmart;

import android.app.Application;
import android.util.Log;

import com.quattrofolia.balansiosmart.models.Session;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class BalansioSmart extends Application {
    private final static String TAG = "BalansioSmart";
    private Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        realm.close();
    }

    public static Session currentSession(Realm realm) {

        /* Return the latest sesssion from database
         * or create and return a new session without
         * userId */

        RealmResults<Session> sessions = realm.where(Session.class).findAll();
        if (!sessions.isEmpty()) {
            if (sessions.size() > 1) {
                Log.e(TAG, "Sessions size should never be  more than 1. Is " + sessions.size());
                return sessions.last();
            }
            return sessions.get(0);
        } else {
            Session s = new Session();
            s.setPrimaryKey(s.getNextPrimaryKey(realm));
            return realm.copyToRealm(s);
        }
    }
}