package com.quattrofolia.balansiosmart.goalComposer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

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

    public static final String TAG = "FragmentActivity";
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
        sessionResultsListener = new RealmChangeListener<RealmResults<Session>>() {
            @Override
            public void onChange(RealmResults<Session> sessions) {
                if (sessions.size() == 1) {
                    final int id = sessions.first().getUserId().intValue();
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Incrementable incrementableGoal = goal;
                            incrementableGoal.setPrimaryKey(incrementableGoal.getNextPrimaryKey(realm));
                            realm.copyToRealmOrUpdate((RealmObject) incrementableGoal);
                            User managedUser = realm.where(User.class).equalTo("id", id).findFirst();
                            managedUser.getGoals().add((Goal) incrementableGoal);
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            finish();
                        }
                    });
                } else {
                    displayAuthErrorDialog();
                }
            }
        };
        sessionResults = realm.where(Session.class).findAllAsync();
        sessionResults.addChangeListener(sessionResultsListener);
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
