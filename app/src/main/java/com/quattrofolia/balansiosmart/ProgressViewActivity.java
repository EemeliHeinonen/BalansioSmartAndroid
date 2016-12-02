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
import com.quattrofolia.balansiosmart.models.Session;
import com.quattrofolia.balansiosmart.models.User;
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
        createGoalButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(ProgressViewActivity.this, GoalComposerActivity.class);
                startActivity(i);
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

        /* Instantiate Realm for ProgressView's UI thread.
        Instantiate RealmChangeListener for observing any changes
        in the model and updating the view accordingly. */

        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object element) {
                sessionResults = realm.where(Session.class).findAllAsync();
            }
        };
        realm.addChangeListener(realmChangeListener);

        sessionResultsListener = new RealmChangeListener<RealmResults<Session>>() {
            @Override
            public void onChange(RealmResults<Session> sessionResults) {
                // Received sessionResults
                if (!sessionResults.isEmpty()) {

                    /* Session found.
                    * Get user object by id.
                    * Populate adapter datasets with responding data. */

                    Session currentSession = sessionResults.last();
                    User managedUser = realm.where(User.class).equalTo("id", currentSession.getUserId().intValue()).findFirst();
                    userNameTextView.setText("#" + managedUser.getId() + ": " + managedUser.getFirstName() + " " + managedUser.getLastName());
                    setInterfaceAccessibility(true);
                    for (Goal g : managedUser.goals) {
                        Log.d(TAG, g.getType().getLongName());
                    }
                    goalItems.addAll(managedUser.goals);
                } else {

                    /* Session not found.
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
        if (authorized) {
            userNameTextView.setVisibility(View.VISIBLE);
            cardStack.setVisibility(View.VISIBLE);
            createGoalButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            createMockUserButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
        } else {
            userNameTextView.setVisibility(View.INVISIBLE);
            cardStack.setVisibility(View.INVISIBLE);
            createGoalButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.INVISIBLE);
            createMockUserButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sessionResults.removeChangeListener(sessionResultsListener);
        realm.removeChangeListener(realmChangeListener);
        if (sessionResults.size() == 1) {
            storage.save(sessionResults.first());
        }
        realm.close();
    }
}
