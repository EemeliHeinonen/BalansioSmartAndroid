package com.quattrofolia.balansiosmart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.quattrofolia.balansiosmart.cardstack.CardStack;
import com.quattrofolia.balansiosmart.cardstack.CardsDataAdapter;
import com.quattrofolia.balansiosmart.dialogs.SelectUserDialogFragment;
import com.quattrofolia.balansiosmart.dialogs.UserCreatedDialogFragment;
import com.quattrofolia.balansiosmart.goalComposer.GoalComposerActivity;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import com.quattrofolia.balansiosmart.models.HealthDataType;
import com.quattrofolia.balansiosmart.models.Incrementable;
import com.quattrofolia.balansiosmart.models.Session;
import com.quattrofolia.balansiosmart.models.User;
import com.quattrofolia.balansiosmart.notifications.NotificationEventReceiver;
import com.quattrofolia.balansiosmart.storage.Storage;

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

    // DEBUG
    private RealmChangeListener<Realm> realmChangeListener;

    // View
    private CardStack cardStack;
    private CardsDataAdapter cardAdapter;
    private Button createGoalButton;
    private Button defaultGoalsButton;
    private List<Goal> goalItems;
    private RecyclerView goalRecyclerView;
    private GoalItemRecyclerAdapter goalAdapter;
    private LinearLayoutManager goalLayoutManager;
    private TextView userNameTextView;

    // Storage
    private Realm realm;
    private Storage storage;
    private RealmResults<Session> sessionResults;
    private RealmChangeListener<RealmResults<Session>> sessionResultsListener;

    // Model

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_view);

        realm = Realm.getDefaultInstance();
        realmChangeListener = new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm element) {
                Session s = BalansioSmart.currentSession(element);
                Integer uid = s.getUserId();
                if (uid == null) {
                    return;
                }
                User u = element.where(User.class).equalTo("id", uid).findFirst();
                Log.d(TAG, "USER #" + u.getId() + ": " + u.getFirstName() + " " + u.getLastName());
                for (Goal g : u.getGoals()) {
                    Log.d(TAG, "GOAL: " + g.getId() + "/ " + g.getType().getLongName());
                    Log.d(TAG, "DISCIPLINE: " + g.getDiscipline().getFrequency() + " times a " + g.getDiscipline().getMonitoringPeriod().toString());
                }
            }
        };
        realm.addChangeListener(realmChangeListener);

        // Use storage.save() for saving autoincrementable objects
        storage = new Storage();

        userNameTextView = (TextView) findViewById(R.id.textView_userName);

        /* Goal RecyclerView */
        goalRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_goals);
        goalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        goalRecyclerView.setLayoutManager(goalLayoutManager);
        goalItems = new ArrayList<>();
        goalAdapter = new GoalItemRecyclerAdapter(goalItems);
        goalRecyclerView.setAdapter(goalAdapter);

        /* Button bar */
        createGoalButton = (Button) findViewById(R.id.button_createGoal);
        defaultGoalsButton = (Button) findViewById(R.id.default_goals_button);

        createGoalButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(ProgressViewActivity.this, GoalComposerActivity.class);
                startActivity(i);
            }
        });


        defaultGoalsButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Create default goals and entries here
                final Session session = BalansioSmart.currentSession(realm);
                final HealthDataEntry firstEntry = new HealthDataEntry();
                firstEntry.setType(HealthDataType.WEIGHT);
                firstEntry.setValue(new BigDecimal("4.5"));
                firstEntry.setInstant(new Instant());

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
            * Save goal and add it to user's healthDataEntries of goals. */

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            Incrementable incrementable = firstEntry;
                            incrementable.setPrimaryKey(incrementable.getNextPrimaryKey(bgRealm));
                            bgRealm.copyToRealmOrUpdate((RealmObject) incrementable);
                            User managedUser = bgRealm.where(User.class).equalTo("id", id).findFirst();
                            managedUser.getEntries().add((HealthDataEntry) incrementable);
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm bgRealm) {
                                    User updatedUser = bgRealm.where(User.class).equalTo("id", session.getUserId().intValue()).findFirst();
                                    if (updatedUser != null) {
                                        Log.d(TAG, "Entries updated. Total amount of entries is " + updatedUser.getEntries().size());
                                        for (HealthDataEntry updatedEntry : updatedUser.getEntries()) {
                                            Log.d(TAG, "Entry type: " + updatedEntry.getType().getLongName());
                                            Log.d(TAG, "execute: " + updatedEntry.getInstant().toString());
                                        }
                                    }
                                }
                            });
                            //finish();
                        }
                    });

                } else {
                    //displayAuthErrorDialog();
                    Log.d(TAG, "create entries onClick: Session is null");
                }
            }
        });

        /////// user creation and login/////////////
        final RealmResults<User> users = realm.where(User.class).findAll();

        if(users.size() == 0){
            String fName = "Joe";
            String lName = "Type 2 diabetic";
            storage.save(new User(fName, lName));
        }

            realm.executeTransactionAsync(new Realm.Transaction() {
                                              @Override
                                              public void execute(Realm realm) {
                                                  RealmResults<User> managedUsers;
                                                  managedUsers = realm.where(User.class).findAll();
                                                  final User selected = managedUsers.get(0);
                                                  RealmResults<Session> managedSessions;
                                                  managedSessions = realm.where(Session.class).findAll();
                                                  if (managedSessions.size() == 1) {
                                                      managedSessions.get(0).setUserId(selected.getId());
                                                  } else {
                                                      Log.e(TAG, "managed sessions size shouldn't be " + managedSessions.size());
                                                  }
                                              }
                                          },
                    new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            RealmResults<User> managedUsers;
                            managedUsers = realm.where(User.class).findAll();
                            final User selected = managedUsers.get(0);
                            Log.d(TAG, "User " + selected.getId() + " logged in");
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            error.printStackTrace();
                        }
                    });


        ////////////////////////////////////////////


        cardStack = (CardStack) findViewById(R.id.cardStack);
        cardStack.setContentResource(R.layout.card_content);
        cardAdapter = new CardsDataAdapter(getApplicationContext());
        cardAdapter.add("test1");
        cardAdapter.add("test2");
        cardAdapter.add("test3");
        cardAdapter.add("test4");
        cardAdapter.add("test5");
        cardStack.setAdapter(cardAdapter);

        /* Instantiate Realm for ProgressView's UI thread.
        Instantiate RealmChangeListener for observing any changes
        in the model and updating the view accordingly. */

        sessionResultsListener = new RealmChangeListener<RealmResults<Session>>() {
            @Override
            public void onChange(RealmResults<Session> sessionResults) {
                Boolean authorized = false;
                goalItems.clear();

                if (!sessionResults.isEmpty()) {
                    if (sessionResults.size() > 1) {
                        Log.e(TAG, "sessionResults size shouldn't be " + sessionResults.size());
                    }

                    // Get last session.
                    Session currentSession = sessionResults.last();

                    if (currentSession.getUserId() != null) {

                        /* userId found: User is logged in.
                        * Get user object by id.
                        * Update interface.
                        * Populate adapter datasets with responding data. */

                        User managedUser;
                        managedUser = realm.where(User.class).equalTo("id", currentSession.getUserId().intValue()).findFirst();
                        userNameTextView.setText("User: " + managedUser.getFirstName() + " " + managedUser.getLastName());
                        authorized = true;
                        goalItems.addAll(managedUser.getGoals());
                    }
                } else {
                    storage.save(new Session());

                }

                /* Update UI and adapters. */

                setInterfaceAccessibility(authorized);
                goalAdapter.setItemList(goalItems);
            }
        };
        sessionResults = realm.where(Session.class).findAllAsync();
        sessionResults.addChangeListener(sessionResultsListener);
        NotificationEventReceiver.setupAlarm(getApplicationContext());

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        goalItems.clear();

        Session currentSession = sessionResults.last(null);
        if (currentSession == null || currentSession.getUserId() == null) {
            return;
        }

        User user = realm.where(User.class).equalTo("id", currentSession.getUserId()).findFirst();
        goalItems.addAll(user.getGoals());
        goalAdapter.setItemList(goalItems);
    }


    private void setInterfaceAccessibility(boolean authorized) {
        createGoalButton.setEnabled(authorized);
        defaultGoalsButton.setEnabled(authorized);
        if (authorized) {
            userNameTextView.setVisibility(View.VISIBLE);
            cardStack.setVisibility(View.VISIBLE);
            defaultGoalsButton.setVisibility(View.VISIBLE);
        } else {
            userNameTextView.setVisibility(View.INVISIBLE);
            cardStack.setVisibility(View.INVISIBLE);
            defaultGoalsButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sessionResults.removeChangeListeners();
        realm.removeAllChangeListeners();
        realm.close();
    }
}
