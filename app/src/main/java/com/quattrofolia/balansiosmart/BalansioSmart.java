package com.quattrofolia.balansiosmart;

import android.app.Application;
import android.util.Log;

import com.quattrofolia.balansiosmart.models.Session;
import com.quattrofolia.balansiosmart.models.User;
import com.quattrofolia.balansiosmart.storage.Storage;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class BalansioSmart extends Application {
    private final static String TAG = "BalansioSmart";
    private Realm realm;
    private RealmChangeListener sessionResultsListener;
    private RealmResults<Session> sessionResults;
    private Storage storage;
    private User user;

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

                /* Instantiate RealmChangeListener for observing Session objects.
        *  In the listener manage authorization between session userId
        *  and view's User object. If authorized, create a query for
        *  the User object and register a listener. Otherwise remove
        *  listeners and uninstantiate. Finally call the function that
        *  refreshes the view for the user. */

        sessionResultsListener = new RealmChangeListener<RealmResults<Session>>() {
            @Override
            public void onChange(RealmResults<Session> sessionResults) {
                if (sessionResults.isEmpty()) {
                    storage.save(new Session());
                } else {
                    if (sessionResults.size() > 1) {
                        Log.e(TAG, "sessionResults size shouldn't be " + sessionResults.size());
                    }
                    Session lastSession = sessionResults.last();
                    boolean loggedIn = (lastSession.getUserId() != null);
                    boolean previousUserFound = (user != null);

                    if (loggedIn) {

                        int userId = lastSession.getUserId().intValue();

                        if (previousUserFound) {

                            boolean authorized = (user.getId() == userId);

                        }

                    } else {

                        /* This block automatically creates a user and logs in. */

                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                User u = new User("Joe", "with Type 2 diabetes");
                                u.setPrimaryKey(u.getNextPrimaryKey(realm));
                                realm.copyToRealm(u);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                int userId = realm.where(User.class).findAll().last().getId();
                                realm.beginTransaction();
                                Session s = new Session(userId);
                                s.setPrimaryKey(s.getNextPrimaryKey(realm));
                                realm.copyToRealm(s);
                                realm.commitTransaction();
                            }
                        });
                    }
                }
            }
        };
        sessionResults = realm.where(Session.class).findAllAsync();
        sessionResults.addChangeListener(sessionResultsListener);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        realm.close();
    }

    public static Session currentSession(final Realm realm) {

        /* Return the latest session from database
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
            realm.beginTransaction();
            Session s = new Session();
            s.setPrimaryKey(s.getNextPrimaryKey(realm));
            realm.copyToRealm(s);
            realm.commitTransaction();
            return s;
        }
    }
}