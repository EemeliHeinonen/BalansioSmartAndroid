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
import com.quattrofolia.balansiosmart.goalList.GoalItemRecyclerAdapter;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.NotificationEntry;
import com.quattrofolia.balansiosmart.models.Session;
import com.quattrofolia.balansiosmart.models.User;
import com.quattrofolia.balansiosmart.notifications.NotificationEventReceiver;
import com.quattrofolia.balansiosmart.storage.Storage;

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
        createGoalButton = (Button) findViewById(R.id.button_createGoal);

        createGoalButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(ProgressViewActivity.this, GoalComposerActivity.class);
                startActivity(i);
            }
        });

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
                                storage.save(new Session(realm.where(User.class).findAll().last().getId()));
                            }
                        });
                    }
                    setInterfaceForUser(user);
                }
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
        createCards();
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
    private void createCards(){
        RealmResults<NotificationEntry> allNotificationEntries = realm.where(NotificationEntry.class).findAll();
        for(NotificationEntry entry : allNotificationEntries){
            cardAdapter.insert(entry.getNotificationText().toString(),0);
        }
    }
}
