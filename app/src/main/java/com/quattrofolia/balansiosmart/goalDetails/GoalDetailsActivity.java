package com.quattrofolia.balansiosmart.goalDetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quattrofolia.balansiosmart.BalansioSmart;
import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.dialogs.DeleteGoalDialogFragment;
import com.quattrofolia.balansiosmart.goalComposer.ComposerMode;
import com.quattrofolia.balansiosmart.goalComposer.GoalComposerActivity;
import com.quattrofolia.balansiosmart.models.Discipline;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import com.quattrofolia.balansiosmart.models.Range;
import com.quattrofolia.balansiosmart.models.Session;
import com.quattrofolia.balansiosmart.models.User;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class GoalDetailsActivity extends AppCompatActivity {

    private TextView goalTypeHeader;
    private LinearLayout goalDetailContainer;

    private Button buttonEditGoal;
    private Button buttonDeleteGoal;

    private Realm realm;
    private User user;
    private Goal goal;
    private RealmResults<Goal> goalResults;
    private RealmResults<HealthDataEntry> healthDataEntries;


    private List<Pair<String, String>> goalSettings;
    ValuePairViewAdapter goalSettingsAdapter;
    RecyclerView goalSettingsView;


    private void findViewComponents() {
        goalTypeHeader = (TextView) findViewById(R.id.textView_goalTypeHeader);
        goalDetailContainer = (LinearLayout) findViewById(R.id.layout_goalDetailContainer);
        buttonEditGoal = (Button) findViewById(R.id.button_editGoal);
        buttonDeleteGoal = (Button) findViewById(R.id.button_deleteGoal);
        goalSettingsView = (RecyclerView) findViewById(R.id.recyclerView_goalSettings);
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

        goalResults = realm.where(Goal.class).findAllAsync();
        goalResults.addChangeListener(new RealmChangeListener<RealmResults<Goal>>() {
            @Override
            public void onChange(RealmResults<Goal> element) {
                goal = getGoal();
                boolean deleted = (goal == null);
                if (deleted) {
                    finish();
                } else {
                    showGoalDetails(goal);
                }
            }
        });

        // Set up the recycler views

        goalSettings = new ArrayList<>();
        goalSettingsAdapter = new ValuePairViewAdapter(goalSettings);
        goalSettingsView.setAdapter(goalSettingsAdapter);
        goalSettingsView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_list_view);
        GoalDetailsRecyclerViewAdapter adapter = new GoalDetailsRecyclerViewAdapter(healthDataEntries);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonEditGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goalComposerActivity = new Intent(view.getContext(), GoalComposerActivity.class)
                        .putExtra(ComposerMode.EDIT.toString(), goal.getId());
                startActivity(goalComposerActivity);
            }
        });

        buttonDeleteGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteGoalDialogFragment f = DeleteGoalDialogFragment.newInstance(goal.getId());
                f.show(getFragmentManager(), "Delete Goal?");
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
        goalTypeHeader.setText(goal.getType().getLongName());

        // Show disciplines reading
        Discipline discipline = goal.getDiscipline();

        List<Pair<String, String>> settings = new ArrayList<>();

        if (discipline != null) {
            settings.add(new Pair<>("Measure " +
                    goal.getType().getLongName().toLowerCase()
                    , discipline.getDescriptiveName()));
        }

        // Show target range
        Range range = goal.getTargetRange();
        if (range != null) {
            settings.add(new Pair<>("Target Range",
                    range.getDescriptiveName()
                            + " "
                            + goal.getType().getUnit().toString()));
        }

        settings.add(new Pair<>("Notification Setting", goal.getNotificationStyle()));

        goalSettingsAdapter.setValuePairs(settings);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
