package com.quattrofolia.balansiosmart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class GoalInitializer extends AppCompatActivity {

    private Button customizedGoals;
    private Button defaultGoals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.goal_initializer);

        customizedGoals = (Button) findViewById(R.id.customizeGoals);
        defaultGoals = (Button) findViewById(R.id.defaultGoals);

        customizedGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        defaultGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
