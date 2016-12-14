package com.quattrofolia.balansiosmart.goalComposer;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.quattrofolia.balansiosmart.BalansioSmart;
import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.dialogs.AuthorizationErrorDialogFragment;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.Incrementable;
import com.quattrofolia.balansiosmart.models.Session;
import com.quattrofolia.balansiosmart.models.User;

import io.realm.Realm;
import io.realm.RealmObject;

//Activity for creating and editing goals, handles the fragments of the GoalComposer
public class GoalComposerActivity extends FragmentActivity {

    public static final String TAG = "GoalComposerActivity";
    private Realm realm;
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

        // if the GoalComposer is opened by a notification action, dismiss the notification afterwards.
        if (getIntent().hasExtra("notificationId")) {
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

            /* Begin checking for enumerated extras */

            Intent i = getIntent();
            if (i.hasExtra(ComposerMode.GENERATE.toString())) {
                MedicalCondition condition = (MedicalCondition) i.getSerializableExtra(ComposerMode.GENERATE.toString());
                Goal generatedGoal = condition.goalPreset();
                Bundle args = new Bundle();
                args.putString("goalType", generatedGoal.getType().toString());
                args.putInt("frequency", generatedGoal.getDiscipline().getFrequency());
                if (generatedGoal.getDiscipline() != null) {
                    args.putString("monitoringPeriod", generatedGoal.getDiscipline().getMonitoringPeriod().toString());
                }
                if (generatedGoal.getTargetRange() != null) {
                    args.putString("rangeMin", generatedGoal.getTargetRange().getLow().toString());
                    args.putString("rangeMax", generatedGoal.getTargetRange().getHigh().toString());
                }
                args.putString("notificationStyle", generatedGoal.getNotificationIntensity().toString());
                GoalOverviewFragment overviewFragment = GoalOverviewFragment.newInstance(args);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, overviewFragment)
                        .commit();
            }

            if (i.hasExtra(ComposerMode.CREATE.toString())) {
                // Create a new Fragment to be placed in the activity layout
                GoalTypeFragment typeFragment = new GoalTypeFragment();

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                typeFragment.setArguments(i.getExtras());

                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, typeFragment).commit();
            }

            if (i.hasExtra(ComposerMode.EDIT.toString())) {
                isEditingGoal = true;
                goalId = i.getIntExtra(ComposerMode.EDIT.toString(), -1);
                Goal goal = realm.where(Goal.class).equalTo("id", goalId).findFirst();
                GoalIntensityFragment intensityFragment = GoalIntensityFragment.newInstance(goal);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.fragment_container, intensityFragment);

                // Commit the transaction
                transaction.commit();
            }

            return;

            /* End checking for enumerated extras */


            //check if the Activity was opened by a notification's edit Goal intent, if it was, navigate to the second fragment.

            /*
            if (getIntent().hasExtra("type")){

                HealthDataType type = (HealthDataType) getIntent().getSerializableExtra("type");

                if (getIntent().hasExtra("goalId")) {
                    Log.d(TAG, "onCreate: has goalId");
                    goalId = getIntent().getIntExtra("goalId", -1);
                    isEditingGoal = true;
                }
                Log.d(TAG, "onCreate: isEditingGoal: "+isEditingGoal);
                GoalIntensityFragment newFragment = GoalIntensityFragment.newInstance(type);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.fragment_container, newFragment);

                // Commit the transaction
                transaction.commit();

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
            */


        }
    }

    public boolean isEditingGoal() {
        return isEditingGoal;
    }

    // Edit an already existing Goal object and save the changes to the Realm database
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

    public void addGoal(final Goal goal, final Boolean doFinish) {
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
                if (doFinish) {
                    // Close the activity if the user didn't choose to add another goal.
                    finish();
                }
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

