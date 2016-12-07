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
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;


public class ProgressViewActivity extends Activity {

    private static final String TAG = "ProgressViewActivity";

    // View
    private CardStack cardStack;
    private CardsDataAdapter cardAdapter;
    private Button createGoalButton;
    private Button notificationButton;
    private Button defaultGoalsButton;
    private Button createMockUserButton;
    private Button loginButton;
    private Button logoutButton;
    private List<Goal> goalItems;
    private RecyclerView goalRecyclerView;
    private GoalItemRecyclerAdapter goalAdapter;
    private RecyclerView.LayoutManager goalLayoutManager;
    private TextView userNameTextView;

    // Storage
    private Realm realm;
    private RealmChangeListener realmChangeListener;
    private Storage storage;
    private RealmResults<Session> sessionResults;
    private RealmChangeListener<RealmResults<Session>> sessionResultsListener;

    // Model

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_view);

        realm = Realm.getDefaultInstance();

        // Use storage.save() for saving autoincrementable objects
        storage = new Storage();

        userNameTextView = (TextView) findViewById(R.id.textView_userName);
        goalRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_goals);
        goalRecyclerView.setHasFixedSize(false);
        goalLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        goalItems = new ArrayList<>();
        goalRecyclerView.setLayoutManager(goalLayoutManager);
        goalAdapter = new GoalItemRecyclerAdapter(goalItems);
        goalRecyclerView.setAdapter(goalAdapter);
        createGoalButton = (Button) findViewById(R.id.button_createGoal);
        notificationButton = (Button) findViewById(R.id.notification_button);
        defaultGoalsButton = (Button) findViewById(R.id.default_goals_button);
        createGoalButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(ProgressViewActivity.this, GoalComposerActivity.class);
                startActivity(i);

            }
        });

        notificationButton.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                //Send notifications
                NotificationEventReceiver.setupAlarm(getApplicationContext());
            }
        });

        defaultGoalsButton.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                //Create default goals and entries here
                final Session session = BalansioSmart.currentSession(realm);
                final HealthDataEntry firstEntry = new HealthDataEntry();
                firstEntry.setType(HealthDataType.BLOOD_GLUCOSE);
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
            * Save goal and add it to user's list of goals. */

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            Incrementable incrementable = firstEntry;
                            incrementable.setPrimaryKey(incrementable.getNextPrimaryKey(bgRealm));
                            bgRealm.copyToRealmOrUpdate((RealmObject) incrementable);
                            User managedUser = bgRealm.where(User.class).equalTo("id", id).findFirst();
                            RealmList<HealthDataEntry> entries = managedUser.getEntries();
                            entries.add((HealthDataEntry) incrementable);
                            managedUser.setEntries(entries);
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
                                            Log.d(TAG, "execute: "+updatedEntry.getInstant().toString());
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

        createMockUserButton = (Button) findViewById(R.id.button_createUser);
        createMockUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fName = "Mock";
                String lName = "User";
                storage.save(new User(fName, lName));
                UserCreatedDialogFragment fragment = new UserCreatedDialogFragment();
                fragment.show(getFragmentManager(), "User \"" + fName + " " + lName + "\" created.");
            }
        });
        loginButton = (Button) findViewById(R.id.button_selectUser);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectUserDialogFragment fragment = new SelectUserDialogFragment();
                fragment.show(getFragmentManager(), "Select User:");
            }
        });
        logoutButton = (Button) findViewById(R.id.button_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<Session> sessions = realm.where(Session.class).findAll();
                        sessions.deleteAllFromRealm();
                    }
                });
            }
        });

        cardStack = (CardStack) findViewById(R.id.cardStack);
        cardStack.setContentResource(R.layout.card_content);
        cardAdapter = new CardsDataAdapter(getApplicationContext());
        cardAdapter.add("test1");
        cardAdapter.add("test2");
        cardAdapter.add("test3");
        cardAdapter.add("test4");
        cardAdapter.add("test5");
        cardStack.setAdapter(cardAdapter);

        sessionResultsListener = new RealmChangeListener<RealmResults<Session>>() {
            @Override
            public void onChange(RealmResults<Session> sessionResults) {
                // Received sessionResults
                if (!sessionResults.isEmpty()) {
                    if (sessionResults.size() > 2) {
                        Log.e(TAG, "sessionResults size shouldn't be " + sessionResults.size());
                    }

                    /* Sessions found: User is logged in.
                    * Get last session.
                    * Get user object by id.
                    * Update interface.
                    * Populate adapter datasets with responding data. */

                    Session currentSession = sessionResults.last();
                    User managedUser = realm.where(User.class).equalTo("id", currentSession.getUserId().intValue()).findFirst();
                    userNameTextView.setText("#" + managedUser.getId() + ": " + managedUser.getFirstName() + " " + managedUser.getLastName());
                    setInterfaceAccessibility(true);
                    for (Goal g : managedUser.getGoals()) {
                        Log.d(TAG, g.getType().getLongName());
                    }
                    managedUser.getGoals().size();
                    goalItems.addAll(managedUser.getGoals());
                } else {

                    /* Session not found: User is not logged in.
                    * Update interface.
                    * Clear adapter datasets. */

                    setInterfaceAccessibility(false);
                    goalItems.clear();
                }

                /* Update adapters. */

                goalAdapter.setItemList(goalItems);
            }
        };
        sessionResults = realm.where(Session.class).findAllAsync();
        sessionResults.addChangeListener(sessionResultsListener);
    }

    private void setInterfaceAccessibility(boolean authorized) {
        createGoalButton.setEnabled(authorized);
        logoutButton.setEnabled(authorized);
        createMockUserButton.setEnabled(!authorized);
        loginButton.setEnabled(!authorized);
        notificationButton.setEnabled(authorized);
        defaultGoalsButton.setEnabled(authorized);
        if (authorized) {
            userNameTextView.setVisibility(View.VISIBLE);
            cardStack.setVisibility(View.VISIBLE);
            createGoalButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            createMockUserButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            notificationButton.setVisibility(View.VISIBLE);
            defaultGoalsButton.setVisibility(View.VISIBLE);
        } else {
            userNameTextView.setVisibility(View.INVISIBLE);
            cardStack.setVisibility(View.INVISIBLE);
            createGoalButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.INVISIBLE);
            createMockUserButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            notificationButton.setVisibility(View.INVISIBLE);
            defaultGoalsButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sessionResults.removeChangeListener(sessionResultsListener);
        realm.removeChangeListener(realmChangeListener);
        if (sessionResults.size() > 1) {
            storage.save(sessionResults.last());
        }
        realm.close();
    }
}
