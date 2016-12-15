package com.quattrofolia.balansiosmart.goalDetails;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.quattrofolia.balansiosmart.BalansioSmart;
import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.goalComposer.GoalComposerActivity;
import com.quattrofolia.balansiosmart.models.Discipline;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import com.quattrofolia.balansiosmart.models.Range;
import com.quattrofolia.balansiosmart.models.Session;
import com.quattrofolia.balansiosmart.models.User;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

import static android.widget.Toast.*;


public class GoalDetailsActivity extends AppCompatActivity {

    private TextView goalName;
    private TextView disciplinesReadings;
    private TextView targetRange;
    private TextView notificationFrequency;
    private ImageButton editButton;
    private ImageButton deleteButton;

    private Realm realm;
    private User user;
    private Goal goal;
    private RealmResults<HealthDataEntry> healthDataEntries;


    private void findViewComponents() {
        goalName = (TextView) findViewById(R.id.goalName);
        disciplinesReadings = (TextView) findViewById(R.id.disciplinesReading);
        targetRange = (TextView) findViewById(R.id.targetRange);
        notificationFrequency = (TextView) findViewById(R.id.notificationFrequency);
        editButton = (ImageButton) findViewById(R.id.editButton);
        deleteButton = (ImageButton) findViewById(R.id.deleteButton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

        // Get user, goal and entries
        user = getUser();
        goal = getGoal();
        healthDataEntries = user.getEntries()
                .where()
                .equalTo("type", goal.getType().name())
                .findAll();

        setContentView(R.layout.activity_goal_details);
        findViewComponents();

        showGoalDetails(goal);

        goal.addChangeListener(new RealmChangeListener<Goal>() {
            @Override
            public void onChange(Goal goal) {
                showGoalDetails(goal);
            }
        });

        // Set up the recycler view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_list_view);
        GoalDetailsRecyclerViewAdapter adapter = new GoalDetailsRecyclerViewAdapter(healthDataEntries);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add click listener for edit goal button
        final Activity activity = this;
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goalComposerActivity = new Intent(activity, GoalComposerActivity.class)
                        .putExtra("type", goal.getType().toString())
                        .putExtra("goalId", goal.getId());
                startActivity(goalComposerActivity);
            }
        });

        // Add click listener for the delete goal button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmList<HealthDataEntry> userEntries = user.getEntries();
                        userEntries.removeAll(healthDataEntries);
                        healthDataEntries.deleteAllFromRealm();

                        RealmList<Goal> userGoals = user.getGoals();
                        userGoals.remove(goal);
                        goal.deleteFromRealm();
                        Toast.makeText(activity, "Goal deleted", LENGTH_LONG).show();
                        activity.finish();
                    }
                });

            }
        });
    }

    private User getUser() {
        // Verify Session and instantiate User
        Session session = BalansioSmart.currentSession(realm);
        Integer userId = session.getUserId();
        if (userId == null) {
            finish();
        }
        return realm.where(User.class)
                .equalTo("id", userId)
                .findFirst();
    }

    private Goal getGoal() {
        // Get Goal object by the id from intent
        Intent intent = getIntent();
        int goalId = intent.getIntExtra("GOAL_ID", -1);

        if (goalId == -1) {
            // No id found
            finish();
        }
        return realm.where(Goal.class)
                .equalTo("id", goalId)
                .findFirst();
    }

    private void showGoalDetails(Goal goal) {
        // Show goal name
        goalName.setText(goal.getType().getLongName());

        // Show disciplines reading
        Discipline discipline = goal.getDiscipline();
        if (discipline != null) {
            disciplinesReadings.setText(discipline.getFrequency() + " times a " + discipline.getMonitoringPeriod().name());
        }

        // Show target range
        Range range = goal.getTargetRange();
        if (range != null) {
            targetRange.setText(range.getLow() + " - " + range.getHigh());
        }

        // Show notification frequency
        notificationFrequency.setText(goal.getNotificationStyle());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
