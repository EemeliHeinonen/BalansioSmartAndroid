package com.quattrofolia.balansiosmart;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import com.quattrofolia.balansiosmart.storage.Storage;

import java.util.List;

import io.realm.RealmResults;


public class GoalDetailsActivity extends AppCompatActivity {

    private ImageButton deleteButton;

    GoalDetailsRecyclerViewAdapter adapter;
    RealmResults<HealthDataEntry> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_details);

        Storage storage = new Storage();
        list = storage.findAll(HealthDataEntry.class);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_list_view);
        adapter = new GoalDetailsRecyclerViewAdapter(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        deleteButton = (ImageButton) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = GoalDetailsActivity.this.getLayoutInflater();
                View content = inflater.inflate(R.layout.activity_goal_details_edit, null);
                final Button deleteData = (Button) content.findViewById(R.id.deleteData);
                final Button deleteAll = (Button) content.findViewById(R.id.deleteAll);
                final Button deleteGoal = (Button) content.findViewById(R.id.deleteGoal);

                deleteData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        adapter.deleteData();
                    }
                });

                deleteAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.deleteAll();
                    }
                });

                deleteGoal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.deleteGoal();
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(GoalDetailsActivity.this);
                builder.setView(content)
                        .setTitle("Delete Menu");
                AlertDialog dialog = builder.create();
                dialog.show();

            }

        });


        private void myToggleSelection(int idx) {
            adapter.toggleSelection(idx);
            String title = getString(
                    R.string.selected_count,
                    adapter.getSelectedItemCount());
            actionMode.setTitle(title);
        }

    }

}
