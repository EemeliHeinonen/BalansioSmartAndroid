package com.quattrofolia.balansiosmart.goalComposer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.quattrofolia.balansiosmart.BalansioSmart;
import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.dialogs.AuthorizationErrorDialogFragment;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.Incrementable;
import com.quattrofolia.balansiosmart.models.Session;
import com.quattrofolia.balansiosmart.models.User;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class GoalComposerActivity extends FragmentActivity {

    public static final String TAG = "GoalComposerActivity";
    private Realm realm;
    private RealmChangeListener<RealmResults<Session>> sessionResultsListener;
    private RealmResults<Session> sessionResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_main);

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

    public void addGoal(final Goal goal) {
        Session session = BalansioSmart.currentSession(realm);
        if (session.getUserId() == null) {
            displayAuthErrorDialog();
            return;
        }

        // DEBUG:
        Integer uid = session.getUserId();
        User u = realm.where(User.class).equalTo("id", uid).findFirst();
        Log.d(TAG, "USER #" + u.getId() + ": " + u.getFirstName() + " " + u.getLastName());
        for (Goal g : u.getGoals()) {
            Log.d(TAG, "GOAL #" + g.getId() + ": " + g.getType().getLongName());
            Log.d(TAG, "DISCIPLINE #" + g.getDiscipline().getId() + ": " + g.getDiscipline().getFrequency() + " times a " + g.getDiscipline().getMonitoringPeriod().toString());
        }
        Log.d(TAG, "COMPOSED GOAL:  " + goal.getType().getLongName());
        Log.d(TAG, "DISCIPLINE #" + goal.getDiscipline().getId() + ": " + goal.getDiscipline().getFrequency() + " times a " + goal.getDiscipline().getMonitoringPeriod().toString());


        final int id = session.getUserId().intValue();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Incrementable incrementableGoal = goal;
                incrementableGoal.setPrimaryKey(incrementableGoal.getNextPrimaryKey(realm));
                Incrementable incrementableDiscipline = goal.getDiscipline();
                incrementableDiscipline.setPrimaryKey(incrementableDiscipline.getNextPrimaryKey(realm));
                realm.copyToRealmOrUpdate((RealmObject) incrementableGoal);
                User managedUser = realm.where(User.class).equalTo("id", id).findFirst();
                managedUser.getGoals().add((Goal) incrementableGoal);
                Log.d(TAG, "MANAGED USER #" + managedUser.getId() + ": " + managedUser.getFirstName() + " " + managedUser.getLastName());
                for (Goal g : managedUser.getGoals()) {
                    Log.d(TAG, "MANAGED GOAL #" + g.getId() + ": " + g.getType().getLongName());
                    Log.d(TAG, "MANAGED DISCIPLINE #" + g.getDiscipline().getId() + ": " + g.getDiscipline().getFrequency() + " times a " + g.getDiscipline().getMonitoringPeriod().toString());
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                finish();

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void displayAuthErrorDialog() {
        AuthorizationErrorDialogFragment fragment = new AuthorizationErrorDialogFragment();
        try {
            fragment.show(getFragmentManager(), "Login Error");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
