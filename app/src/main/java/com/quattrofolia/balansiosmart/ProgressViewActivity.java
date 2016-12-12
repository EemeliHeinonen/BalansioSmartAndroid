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
import com.quattrofolia.balansiosmart.goalList.GoalItemRecyclerAdapter;
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
    private LinearLayoutManager goalLayoutManager;
    private TextView userNameTextView;

    // Storage
    private Realm realm;
    private Storage storage;
    private RealmResults<Session> sessionResults;
    private RealmChangeListener<RealmResults<Session>> sessionResultsListener;
    private User user;
    private RealmChangeListener<User> userListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_view);

        realm = Realm.getDefaultInstance();

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
        notificationButton = (Button) findViewById(R.id.notification_button);
        defaultGoalsButton = (Button) findViewById(R.id.default_goals_button);

        createGoalButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(ProgressViewActivity.this, GoalComposerActivity.class);
                startActivity(i);
            }
        });

        notificationButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Send notifications
                NotificationEventReceiver.setupAlarm(getApplicationContext());
            }
        });

        defaultGoalsButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                final HealthDataEntry firstEntry = new HealthDataEntry();
                final int userId = user.getId();
                firstEntry.setType(HealthDataType.WEIGHT);
                firstEntry.setValue(new BigDecimal("4.5"));
                firstEntry.setInstant(new Instant());

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        Incrementable incrementable = firstEntry;
                        incrementable.setPrimaryKey(incrementable.getNextPrimaryKey(bgRealm));
                        User managedUser = bgRealm.where(User.class).equalTo("id", userId).findFirst();
                        managedUser.getEntries().add((HealthDataEntry) incrementable);
                    }
                });
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
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<Session> managedSessions;
                        managedSessions = realm.where(Session.class).findAll();
                        if (managedSessions.size() == 1) {
                            managedSessions.get(0).setUserId(null);
                        } else {
                            Log.e(TAG, "Amount of managed sessions: " + managedSessions.size());

                            for (int i = 0; i < managedSessions.size(); i++) {
                                if (i == managedSessions.size() - 1) {
                                    managedSessions.get(i).setUserId(null);
                                    break;
                                } else {
                                    managedSessions.deleteFromRealm(i);
                                }
                            }
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "User logged out");
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
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


        userListener = new RealmChangeListener<User>() {
            @Override
            public void onChange(User element) {
                setInterfaceForUser(element);
            }
        };

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
                    Session currentSession = sessionResults.last();
                    boolean loggedIn = (currentSession.getUserId() != null);
                    boolean previousUserFound = (user != null);

                    if (loggedIn) {

                        int userId = currentSession.getUserId().intValue();

                        if (previousUserFound) {

                            boolean authorized = (user.getId() == userId);

                            if (!authorized) {
                                user.removeChangeListeners();
                            }
                        }
                        user = realm.where(User.class).equalTo("id", userId).findFirst();
                        user.addChangeListener(userListener);
                    } else {
                        if (previousUserFound) {
                            user.removeChangeListeners();
                            user = null;
                        }
                    }
                    setInterfaceForUser(user);
                }
            }
        };

        sessionResults = realm.where(Session.class).findAllAsync();
        sessionResults.addChangeListener(sessionResultsListener);
    }

    @Override
    protected void onResume() {
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

        setInterfaceAccessibility(userExists);
        goalAdapter.setItemList(goalItems);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sessionResults.removeChangeListeners();
        realm.removeAllChangeListeners();
        realm.close();
    }
}
