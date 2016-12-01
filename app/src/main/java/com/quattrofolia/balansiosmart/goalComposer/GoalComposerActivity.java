package com.quattrofolia.balansiosmart.goalComposer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.quattrofolia.balansiosmart.AuthorizationErrorDialogFragment;
import com.quattrofolia.balansiosmart.BalansioSmart;
import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.Incrementable;
import com.quattrofolia.balansiosmart.models.Session;
import com.quattrofolia.balansiosmart.models.User;
import com.quattrofolia.balansiosmart.storage.Storage;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class GoalComposerActivity extends FragmentActivity {

    public static final String TAG = "FragmentActivity";
    private Realm realm;
    private Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        storage = new Storage(realm);
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
        final Session session = BalansioSmart.currentSession(realm);

        if (session != null) {

            final int id = session.getUserId().intValue();
            final RealmResults<User> users;
            users = realm.where(User.class).equalTo("id", id).findAll();

            if (users.size() != 1) {
                Log.e(TAG, "Incorrect results");
                displayAuthErrorDialog();
                return;
            }

            /* Session/User database match.
            * Set incrementable primary key for goal.
            * Save goal and add it to user's list of goals. */

            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    Incrementable incrementable = goal;
                    incrementable.setPrimaryKey(incrementable.getNextPrimaryKey(bgRealm));
                    bgRealm.copyToRealmOrUpdate((RealmObject) incrementable);
                    User managedUser = bgRealm.where(User.class).equalTo("id", id).findFirst();
                    managedUser.goals.add((Goal) incrementable);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            User updatedUser = bgRealm.where(User.class).equalTo("id", session.getUserId().intValue()).findFirst();
                            if (updatedUser != null) {
                                Log.d(TAG, "Goals updated. Total amount of goals is " + updatedUser.goals.size());
                                for (Goal updatedGoal : updatedUser.goals) {
                                    Log.d(TAG, "Goal type: " + updatedGoal.getType().getLongName());
                                }
                            }
                        }
                    });
                    finish();
                }
            });

        } else {
            displayAuthErrorDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void displayAuthErrorDialog() {
        AuthorizationErrorDialogFragment fragment = new AuthorizationErrorDialogFragment();
        fragment.show(getFragmentManager(), "Login Error");
    }
}
