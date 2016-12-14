package com.quattrofolia.balansiosmart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;


import com.quattrofolia.balansiosmart.cardstack.CardStack;
import com.quattrofolia.balansiosmart.cardstack.CardsDataAdapter;
import com.quattrofolia.balansiosmart.goalComposer.ComposerMode;
import com.quattrofolia.balansiosmart.goalComposer.GoalComposerActivity;
import com.quattrofolia.balansiosmart.goalList.GoalItemRecyclerAdapter;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import com.quattrofolia.balansiosmart.models.HealthDataType;
import com.quattrofolia.balansiosmart.models.Incrementable;
import com.quattrofolia.balansiosmart.models.NotificationEntry;
import com.quattrofolia.balansiosmart.models.Session;
import com.quattrofolia.balansiosmart.models.User;
import com.quattrofolia.balansiosmart.notifications.NotificationEventReceiver;
import com.quattrofolia.balansiosmart.storage.Storage;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;


public class ProgressViewActivity extends Activity {

    private static final String TAG = "ProgressViewActivity";

    // View
    private CardStack cardStack;
    private CardsDataAdapter cardAdapter;
    private Button hiddenButton;
    private List<Goal> goalItems;
    private RecyclerView goalRecyclerView;
    private GoalItemRecyclerAdapter goalAdapter;
    private LinearLayoutManager goalLayoutManager;
    private TextView userNameTextView;
    private int stepCounter = 0;

    // Storage
    private Realm realm;
    private Storage storage;
    private RealmResults<Session> sessionResults;
    private RealmChangeListener<RealmResults<Session>> sessionResultsListener;
    private User user;

    private RealmResults<User> userResults;
    private RealmChangeListener<RealmResults<User>> userResultsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_view);

        realm = Realm.getDefaultInstance();

        // Use storage.save() for saving autoincrementable objects
        storage = new Storage();

        userNameTextView = (TextView) findViewById(R.id.textView_userName);

        cardStack = (CardStack) findViewById(R.id.cardStack);
        cardStack.setContentResource(R.layout.card_content);
        cardAdapter = new CardsDataAdapter(getApplicationContext());
        cardAdapter.add("Welcome to Balansio Smart!");

        /* Goal RecyclerView */
        goalRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_goals);
        goalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        goalRecyclerView.setLayoutManager(goalLayoutManager);
        goalItems = new ArrayList<>();
        goalAdapter = new GoalItemRecyclerAdapter(goalItems);
        goalRecyclerView.setAdapter(goalAdapter);

        /* Button bar */
        hiddenButton = (Button) findViewById(R.id.button_hidden);
        //hiddenButton.setBackgroundColor(Color.TRANSPARENT);
        final Session session = BalansioSmart.currentSession(realm);


        hiddenButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                stepCounter++;

                switch (stepCounter) {
                    case 1:
                        //Create default goals and entries here
                        final HealthDataEntry firstEntry = new HealthDataEntry();
                        firstEntry.setType(HealthDataType.WEIGHT);
                        firstEntry.setValue(new BigDecimal("70"));
                        firstEntry.setInstant(new DateTime().minusHours(2).toInstant());
                        enterEntry(firstEntry,session);
                        final HealthDataEntry secondEntry = new HealthDataEntry();
                        firstEntry.setType(HealthDataType.WEIGHT);
                        firstEntry.setValue(new BigDecimal("69"));
                        firstEntry.setInstant(new DateTime().minusHours(5).toInstant());
                        enterEntry(firstEntry,session);
                        break;
                    case 2:  //monthString = "February";
                        break;
                    default: //monthString = "Invalid month";
                        break;

                }

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProgressViewActivity.this, GoalComposerActivity.class);
                i.putExtra(ComposerMode.CREATE.toString(), "");
                startActivity(i);

            }
        });
        ;

        cardStack.setAdapter(cardAdapter);


        /* Instantiate RealmChangeListener for observing User objects.
        * Whenever a change is detected in the View's current User
        * object, a method is called to refresh the interface. */

        userResultsListener = new RealmChangeListener<RealmResults<User>>() {
            @Override
            public void onChange(RealmResults<User> element) {
                Integer userId = BalansioSmart.currentSession(realm).getUserId();
                if (userId != null) {
                    user = realm.where(User.class).equalTo("id", userId.intValue()).findFirst();
                }
                setInterfaceForUser(user);
            }
        };
        userResults = realm.where(User.class).findAllAsync();
        userResults.addChangeListener(userResultsListener);

        NotificationEventReceiver.setupAlarm(getApplicationContext());

    }

    private void enterEntry(final HealthDataEntry firstEntry, final Session session){
        if (session != null) {

            final int id = session.getUserId().intValue();
            final RealmResults<User> users;
            users = realm.where(User.class).equalTo("id", id).findAll();

            if (users.size() != 1) {
                Log.e(TAG, "Incorrect results");
                return;
            }

            /* Session/User database match.
            * Set incrementable primary key for goal.
            * Save goal and add it to user's list of goals. */

            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    Incrementable incrementable = firstEntry;
                    incrementable.setPrimaryKey(incrementable.getNextPrimaryKey(bgRealm));
                    bgRealm.copyToRealmOrUpdate((RealmObject) incrementable);
                    User managedUser = bgRealm.where(User.class).equalTo("id", id).findFirst();
                    managedUser.addEntry((HealthDataEntry) incrementable);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            User updatedUser = bgRealm.where(User.class).equalTo("id", session.getUserId().intValue()).findFirst();
                        }
                    });
                }
            });

        } else {
            //displayAuthErrorDialog();
            Log.d(TAG, "create entries onClick: Session is null");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        createCards();
        goalItems.clear();

        Session currentSession = BalansioSmart.currentSession(realm);
        if (currentSession == null || currentSession.getUserId() == null) {
            return;
        }

        User user = realm.where(User.class).equalTo("id", currentSession.getUserId()).findFirst();
        goalItems.addAll(user.getGoals());
        goalAdapter.setItemList(goalItems);
    }


    private void setInterfaceAccessibility(boolean authorized) {
        if (authorized) {
            userNameTextView.setVisibility(View.VISIBLE);
            cardStack.setVisibility(View.VISIBLE);
        } else {
            userNameTextView.setVisibility(View.INVISIBLE);
            cardStack.setVisibility(View.INVISIBLE);
        }
    }

    public void setInterfaceForUser(User user) {
        boolean userExists = (user != null);
        goalItems.clear();
        if (userExists) {
            userNameTextView.setText(user.getFirstName() + " " + user.getLastName());
            goalItems.addAll(user.getGoals());
        } else {
            userNameTextView.setText("");
        }

        /* Refresh interface and adapters. */

        //setInterfaceAccessibility(userExists);
        goalAdapter.setItemList(goalItems);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sessionResults.removeChangeListeners();
        realm.removeAllChangeListeners();
        realm.close();
    }

    private void createCards() {
        RealmResults<NotificationEntry> allNotificationEntries = realm.where(NotificationEntry.class).findAll();
        for (NotificationEntry entry : allNotificationEntries) {
            cardAdapter.insert(entry.getNotificationText().toString(), 0);
        }
    }
}
