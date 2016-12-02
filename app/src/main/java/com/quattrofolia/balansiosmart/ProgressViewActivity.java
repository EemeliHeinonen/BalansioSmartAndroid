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
import com.quattrofolia.balansiosmart.goalComposer.GoalComposerActivity;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import com.quattrofolia.balansiosmart.models.HealthDataType;
import com.quattrofolia.balansiosmart.models.Incrementable;
import com.quattrofolia.balansiosmart.models.Session;
import com.quattrofolia.balansiosmart.models.User;
import com.quattrofolia.balansiosmart.storage.Storage;
import com.quattrofolia.balansiosmart.notifications.NotificationEventReceiver;


import org.joda.time.Instant;

import java.math.BigDecimal;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;

import static com.quattrofolia.balansiosmart.BalansioSmart.session;


public class ProgressViewActivity extends Activity {

    private static final String TAG = "ProgressViewActivity";

    // View
    private CardStack cardStack;
    private CardsDataAdapter cardAdapter;
    private Button createGoalButton;
    private Button notificationButton;
    private Button defaultGoalsButton;
    private Button createMockUserButton;
    private Button logoutButton;
    private RecyclerView goalRecyclerView;
    private RecyclerView.Adapter goalAdapter;
    private RecyclerView.LayoutManager goalLayoutManager;
    private TextView userNameTextView;

    // Storage
    private Realm realm;
    private RealmChangeListener realmChangeListener;
    private Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_view);


        /* Instantiate Realm for ProgressView's UI thread.
        Instantiate RealmChangeListener for observing any changes
        in the model and updating the view accordingly. */

        realm = Realm.getDefaultInstance();
        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object element) {
                Log.d(TAG, "Realm onChange");
                updateView();
            }
        };

        realm.addChangeListener(realmChangeListener);

        // Create Storage for saving autoincrementable objects
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

        goalRecyclerView.setLayoutManager(goalLayoutManager);
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
                            managedUser.entries.add((HealthDataEntry) incrementable);
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm bgRealm) {
                                    User updatedUser = bgRealm.where(User.class).equalTo("id", session.getUserId().intValue()).findFirst();
                                    if (updatedUser != null) {
                                        Log.d(TAG, "Entries updated. Total amount of entries is " + updatedUser.entries.size());
                                        for (HealthDataEntry updatedEntry : updatedUser.entries) {
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
                storage.save(new User("Mock", "User"));
                SelectUserDialogFragment fragment = new SelectUserDialogFragment();
                fragment.show(getFragmentManager(), "Select User:");
            }
        });
        logoutButton = (Button) findViewById(R.id.button_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final RealmResults<Session> sessions = realm.where(Session.class).findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
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

        updateView();
    }

    private void updateView() {

        /* Check if user is logged in */

        final Session session = BalansioSmart.currentSession(realm);

        RealmResults<User> userResults = realm.where(User.class).findAll();

        if (session != null && !userResults.isEmpty()) {
            User managedUser = userResults.where().equalTo("id", session.getUserId()).findFirst();
            if (managedUser != null) {

                /* User id and database match.
                * Update view. */

                userNameTextView.setText(managedUser.getFirstName() + " " + managedUser.getLastName());
                goalAdapter = new GoalItemRecyclerAdapter(managedUser.goals);
                goalRecyclerView.setAdapter(goalAdapter);
                setInterfaceAccessibility(true);
                return;
            } else {

                /* User id and database mismatch.
                * Display an alert dialog. */

                AuthorizationErrorDialogFragment fragment = new AuthorizationErrorDialogFragment();
                fragment.show(getFragmentManager(), "Login Error");
            }
        }

        /* No user information available. */
        userNameTextView.setText("User not logged in.");
        setInterfaceAccessibility(false);
    }

    private void setInterfaceAccessibility(boolean authorized) {
        createGoalButton.setEnabled(authorized);
        createMockUserButton.setEnabled(!authorized);
        logoutButton.setEnabled(authorized);
        if (authorized) {
            cardStack.setVisibility(View.VISIBLE);
            createGoalButton.setVisibility(View.VISIBLE);
            createMockUserButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            cardStack.setVisibility(View.INVISIBLE);
            createGoalButton.setVisibility(View.GONE);
            createMockUserButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.removeChangeListener(realmChangeListener);
        realm.close();
    }
}
