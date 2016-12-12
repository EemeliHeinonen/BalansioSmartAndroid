package com.quattrofolia.balansiosmart.goalComposer;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.quattrofolia.balansiosmart.BalansioSmart;
import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.dialogs.AuthorizationErrorDialogFragment;
import com.quattrofolia.balansiosmart.models.Discipline;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.Incrementable;
import com.quattrofolia.balansiosmart.models.Range;
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
    private boolean isEditingGoal;
    private Integer goalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        isEditingGoal = false;
        goalId = null;
        setContentView(R.layout.activity_main);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (getIntent().hasExtra("notificationId")){
            manager.cancel(getIntent().getIntExtra("notificationId", 0));
        }


        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // If we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            /*!getIntent().getExtras().getString("type").isEmpty()*/
            if (getIntent().hasExtra("type")){
                Log.d(TAG, "onCreate: getExtras not empty, type:" + getIntent().getStringExtra("type"));
                if (getIntent().hasExtra("goalId")) {
                    goalId = getIntent().getIntExtra("goalId", -1);
                    Log.d(TAG, "onCreate: goalId:" + goalId);
                    isEditingGoal = true;
                }

                GoalIntensityFragment newFragment = GoalIntensityFragment.newInstance(getIntent().getStringExtra("type"));
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.fragment_container, newFragment);
                // Commit the transaction
                transaction.commit();

                // Create a new Fragment to be placed in the activity layout
                //GoalIntensityFragment intensityFragment = GoalIntensityFragment.newInstance(getIntent().getExtras().getString("type"));

                //getSupportFragmentManager().beginTransaction()
                  //      .add(R.id.fragment_container, intensityFragment).commit();
            } else {
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
    }

    public boolean isEditingGoal() {
        return isEditingGoal;
    }

    public void editGoal(final Goal editedGoal) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                editedGoal.setPrimaryKey(goalId);
                realm.copyToRealmOrUpdate(editedGoal);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                finish();

            }
        });
    }

    public void addGoal(final Goal goal) {
        Session session = BalansioSmart.currentSession(realm);
        if (session.getUserId() == null) {
            displayAuthErrorDialog();
            return;
        }

        final int id = session.getUserId().intValue();
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