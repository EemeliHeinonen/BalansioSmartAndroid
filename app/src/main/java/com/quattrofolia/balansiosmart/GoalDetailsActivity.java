package com.quattrofolia.balansiosmart;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.quattrofolia.balansiosmart.goalComposer.GoalComposerActivity;
import com.quattrofolia.balansiosmart.goalComposer.GoalTypeAdapter;
import com.quattrofolia.balansiosmart.models.Discipline;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import com.quattrofolia.balansiosmart.models.Range;
import com.quattrofolia.balansiosmart.models.User;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static android.widget.Toast.*;

public class GoalDetailsActivity extends AppCompatActivity {

    private ImageButton editButton;

    private Realm realm;
    private GoalTypeAdapter goalTypeAdapter;
    GoalDetailsRecyclerViewAdapter adapter;
    RealmResults<HealthDataEntry> list;

    private RealmChangeListener realmChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

        Intent intent = getIntent();

        // Get the health data type
        String typeName = intent.getStringExtra("type");

        // Get the User
        int userId = intent.getIntExtra("userId", -1);
        User user = realm.where(User.class).equalTo("id", userId).findFirst();
        Log.i("User", user.getFirstName() + " " + user.getLastName());

        // Get the goal
        Goal goal = user.getGoals().where().equalTo("type", typeName).findFirst();
        Log.i("Goal",
                goal.getType().getLongName() +
                ", discipline: " + goal.getDiscipline().getFrequency() +
                ", range: " + goal.getTargetRange().getLow() + "-" + goal.getTargetRange().getHigh() +
                ", notificationStyle: " + goal.getNotificationStyle()
        );

        // Get entries
        list = user.getEntries().where().equalTo("type", typeName).findAll();

        setContentView(R.layout.activity_goal_details);

        final Activity self = this;

        // Show goal name
        TextView goalName = (TextView) findViewById(R.id.goalName);
        goalName.setText(goal.getType().getLongName());

        // Show disciplines reading
        TextView disciplinesReadings = (TextView)findViewById(R.id.disciplinesReading);
        Discipline discipline = goal.getDiscipline();
        disciplinesReadings.setText(discipline.getFrequency() + " times a " + discipline.getMonitoringPeriod().name());

        // Show target range
        TextView targetRange = (TextView) findViewById(R.id.targetRange);
        Range range = goal.getTargetRange();
        targetRange.setText(range.getLow() + " - " + range.getHigh());

        // Show notification frequency
        TextView notificationFrequency = (TextView) findViewById(R.id.notificationFrequency);
        notificationFrequency.setText(goal.getNotificationStyle());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_list_view);
        adapter = new GoalDetailsRecyclerViewAdapter(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        editButton = (ImageButton) findViewById(R.id.deleteButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = GoalDetailsActivity.this.getLayoutInflater();
                View content = inflater.inflate(R.layout.activity_goal_details_edit, null);
                final Button editGoal = (Button) content.findViewById(R.id.editGoal);
                final Button deleteGoal = (Button) content.findViewById(R.id.deleteGoal);

                editGoal.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent goalComposerActivity = new Intent(self, GoalComposerActivity.class);
                        startActivity(goalComposerActivity);
                    }
                });

                deleteGoal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.deleteGoal();
                        Toast.makeText(self, "Goal deleted", LENGTH_LONG).show();
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(GoalDetailsActivity.this);
                builder.setView(content)
                        .setTitle("Edit");
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });

    }

}
