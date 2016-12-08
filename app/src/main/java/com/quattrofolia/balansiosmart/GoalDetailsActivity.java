package com.quattrofolia.balansiosmart;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.quattrofolia.balansiosmart.goalComposer.GoalComposerActivity;
import com.quattrofolia.balansiosmart.goalComposer.GoalTypeAdapter;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import com.quattrofolia.balansiosmart.storage.Storage;

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
        setContentView(R.layout.activity_goal_details);

        final Activity self = this;

        Storage storage = new Storage();
        list = storage.findAll(HealthDataEntry.class);

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
