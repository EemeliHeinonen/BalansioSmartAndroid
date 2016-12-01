package com.quattrofolia.balansiosmart.goalComposer;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.User;

import io.realm.Realm;
import io.realm.RealmList;

//Goal Composer's activity, the goal composer's fragments will be shown in the fragment container in this activity.

public class GoalComposerActivity extends FragmentActivity{
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this); // Initialize Realm only once when the app starts.
        setContentView(R.layout.activity_main);

        //User is created here just for testing purposes
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
        Log.d("jes", "addGoal: Type: "+g.getType().getLongName());
        Log.d("jes", "addGoal: Discipline: "+g.getDiscipline());
        Log.d("jes", "addGoal: Range: "+g.getTargetRange());
    }
}
