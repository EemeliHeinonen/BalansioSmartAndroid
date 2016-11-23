package com.quattrofolia.balansiosmart;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.User;

import io.realm.Realm;
import io.realm.RealmList;



public class GoalComposerActivity extends FragmentActivity{
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this); // Initialize Realm only once when the app starts.
        setContentView(R.layout.activity_main);
        user = new User();
        user.goals = new RealmList<>();

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // If we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            GoalTypeFragment typeFragment = new GoalTypeFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            typeFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, typeFragment).commit();
        }
    }

    public void addGoal(Goal g){
        Log.d("jes", "addGoal: ");
        user.goals.add(g);
        Log.d("jes", "addGoal: Frequency: "+g.getDiscipline().getFrequency());
    }
}
