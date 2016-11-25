package com.quattrofolia.balansiosmart;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.User;

import io.realm.Realm;

import static com.quattrofolia.balansiosmart.BalansioSmart.userId;


public class GoalComposerActivity extends FragmentActivity{

    public static final String TAG = "FragmentActivity";
    private Realm realm;

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

    public void addGoal(final Goal goal){
        Log.d(TAG, "addGoal: ");

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Goal managedGoal = bgRealm.copyToRealmOrUpdate(goal);
                User managedUser = bgRealm.where(User.class).equalTo("id", userId).findFirst();
                if (managedUser != null) {
                    managedUser.goals.add(managedGoal);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        User updatedUser = bgRealm.where(User.class).equalTo("id", userId).findFirst();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
