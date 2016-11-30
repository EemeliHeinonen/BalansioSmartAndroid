package com.quattrofolia.balansiosmart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import com.quattrofolia.balansiosmart.storage.Storage;

import java.util.List;

import io.realm.Realm;

public class GoalDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_details);

        Storage storage = new Storage();
        List<HealthDataEntry> data = storage.findAll(HealthDataEntry.class);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_list_view);
        GoalDetailsRecyclerViewAdapter adapter = new GoalDetailsRecyclerViewAdapter(data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
