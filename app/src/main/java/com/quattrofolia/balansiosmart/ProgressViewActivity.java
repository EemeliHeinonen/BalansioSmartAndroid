package com.quattrofolia.balansiosmart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.quattrofolia.balansiosmart.cardstack.CardStack;
import com.quattrofolia.balansiosmart.models.User;
import com.quattrofolia.balansiosmart.storage.Storage;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static com.quattrofolia.balansiosmart.BalansioSmart.userId;


public class ProgressViewActivity extends Activity {

    private static final String TAG = "ProgressViewActivity";

    // View
    private CardStack mCardStack;
    private CardsDataAdapter mCardAdapter;
    private Button createGoalButton;
    private RecyclerView goalRecyclerView;
    private RecyclerView.Adapter goalAdapter;
    private RecyclerView.LayoutManager goalLayoutManager;

    // Storage
    private Realm realm;
    private RealmChangeListener userResultsListener;
    private RealmResults<User> userResults;
    private Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_view);

        // Create Storage for saving autoincrementable objects
        storage = new Storage() {
            @Override
            public void successHandler() {
                Log.d(TAG, "successHandler");
            }

            @Override
            public void errorHandler() {
                Log.d(TAG, "errorHandler");
            }
        };

        // Instantiate Realm for the UI thread
        realm = Realm.getDefaultInstance();

        // Define result listener for handling results
        userResultsListener = new RealmChangeListener<RealmResults<User>>() {
            @Override
            public void onChange(RealmResults<User> userRealmResults) {
                Log.d(TAG, userRealmResults.size() + " user results");
                for (User user : userRealmResults) {
                    Log.d(TAG, "User id: " + user.getId());
                }
                userId = userRealmResults.last().getId();
                goalAdapter = new GoalItemRecyclerAdapter(userRealmResults.last().goals);
                goalRecyclerView.setAdapter(goalAdapter);
            }
        };
        userResults = realm.where(User.class).findAll();
        userResults.addChangeListener(userResultsListener);

        storage.save(new User("Test", "User"));
        storage.save(new User("Test", "User2"));
        storage.save(new User("Test", "User3"));

        goalRecyclerView = (RecyclerView) findViewById(R.id.goalRecyclerView);
        goalRecyclerView.setHasFixedSize(false);
        goalLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        goalRecyclerView.setLayoutManager(goalLayoutManager);

        createGoalButton = (Button) findViewById(R.id.create_goal_button);

        createGoalButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(ProgressViewActivity.this, GoalComposerActivity.class);
                startActivity(i);
            }
        });

        mCardStack = (CardStack) findViewById(R.id.cardStack);
        mCardStack.setContentResource(R.layout.card_content);
        //mCardStack.setStackMargin(20);

        mCardAdapter = new CardsDataAdapter(getApplicationContext());
        mCardAdapter.add("test1");
        mCardAdapter.add("test2");
        mCardAdapter.add("test3");
        mCardAdapter.add("test4");
        mCardAdapter.add("test5");

        mCardStack.setAdapter(mCardAdapter);

        if (mCardStack.getAdapter() != null) {
            Log.i("MyActivity", "Card Stack size: " + mCardStack.getAdapter().getCount());
        }
        mCardStack.bringToFront();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userResults.removeChangeListener(userResultsListener);
        realm.close();
    }
}
