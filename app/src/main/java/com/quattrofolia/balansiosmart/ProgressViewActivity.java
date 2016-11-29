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
import com.quattrofolia.balansiosmart.models.User;
import com.quattrofolia.balansiosmart.storage.Storage;

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
                SelectUserDialogFragment fragment = new SelectUserDialogFragment();
                fragment.show(getFragmentManager(), "Select User");
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

        Integer id = BalansioSmart.userId;
        RealmResults<User> userResults;
        User managedUser;
        userResults = realm.where(User.class).findAll();
        if (id != null && !userResults.isEmpty()) {
            managedUser = realm.where(User.class).equalTo("id", id).findFirst();
            if (managedUser != null) {

                /* User id and database match.
                * Update view. */

                userNameTextView.setText(managedUser.getFirstName() + " " + managedUser.getLastName());
                goalAdapter = new GoalItemRecyclerAdapter(managedUser.goals);
                goalRecyclerView.setAdapter(goalAdapter);
                enableAuthorizedInterface(true);
                Log.d(TAG, "View updated");
                return;
            } else {

                /* User id and database mismatch.
                * Display an alert dialog. */

                userNameTextView.setText("");
                BalansioSmart.userId = null;
                AuthorizationErrorDialogFragment fragment = new AuthorizationErrorDialogFragment();
                fragment.show(getFragmentManager(), "Error");
            }
        }

        /* No user information available. */
        userNameTextView.setText("User not logged in.");
        enableAuthorizedInterface(false);
    }

    private void enableAuthorizedInterface(boolean authorized) {
        createGoalButton.setEnabled(authorized);
        createMockUserButton.setEnabled(!authorized);
        if (authorized) {
            cardStack.setVisibility(View.VISIBLE);
            createGoalButton.setVisibility(View.VISIBLE);
            createMockUserButton.setVisibility(View.GONE);
        } else {
            cardStack.setVisibility(View.INVISIBLE);
            createGoalButton.setVisibility(View.GONE);
            createMockUserButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.removeChangeListener(realmChangeListener);
        realm.close();
    }
}
