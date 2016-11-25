package com.quattrofolia.balansiosmart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

/**
 * Created by eemeliheinonen on 27/10/2016.
 */

// Fragment class for selcting progress_view_goal_item_row's notification preferneces

public class GoalNotificationFragment extends Fragment {

    private String goalType;
    private int frequency;
    private String monitoringPeriod;
    private String idealRangeMin;
    private String idealRangeMax;
    private RadioGroup radioButtonGroup;
    private RadioButton rbStrict;
    private RadioButton rbEasy;
    private RadioButton rbNone;
    private String notificationStyle = "Strict";

    public static GoalNotificationFragment newInstance
            (String GoalType, int frequency, String monitoringPeriod, String idealRangeMin, String idealRangeMax) {
        GoalNotificationFragment fragment = new GoalNotificationFragment();
        Bundle args = new Bundle();
        args.putString("goalType", GoalType);
        args.putInt("frequency", frequency);
        args.putString("monitoringPeriod", monitoringPeriod);
        args.putString("rangeMin", idealRangeMin);
        args.putString("rangeMax", idealRangeMax);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get data from the previous fragments
        if (getArguments() != null) {
            goalType = getArguments().getString("goalType");
            frequency = getArguments().getInt("frequency");
            monitoringPeriod = getArguments().getString("monitoringPeriod");
            idealRangeMin = getArguments().getString("rangeMin");
            idealRangeMax = getArguments().getString("rangeMax");
            Log.d(TAG, "onCreate: goaltype: "+goalType);
            Log.d(TAG, "onCreate: measurement frequency: "+frequency);
            Log.d(TAG, "onCreate: monitoringPeriod: "+monitoringPeriod);
            Log.d(TAG, "onCreate: ideal range minimum value: "+idealRangeMin);
            Log.d(TAG, "onCreate: ideal range maximum value: "+idealRangeMax);
        } else {
            Log.d(TAG, "onCreate: arguments null");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout myView =(RelativeLayout) inflater.inflate(R.layout.goal_notification_fragment, container, false);
        TextView tv = (TextView) myView.findViewById(R.id.tvNotificationRemind);
        Button btnNext = (Button) myView.findViewById(R.id.btnNotificationNext);
        radioButtonGroup = (RadioGroup)myView.findViewById(R.id.radioGroup);
        rbStrict = (RadioButton)myView.findViewById(R.id.rbStrict);
        rbEasy = (RadioButton)myView.findViewById(R.id.rbEasy);
        rbNone = (RadioButton)myView.findViewById(R.id.rbNone);
        tv.setText("Remind me to measure "+goalType);

        radioButtonGroup.check(R.id.rbStrict);
        radioButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch(checkedId)
                {
                    case R.id.rbStrict:
                        Log.d(TAG, "onCheckedChanged: Strict");
                        notificationStyle = "Strict";
                        rbStrict.setTextColor(Color.parseColor("#be3e82"));
                        rbEasy.setTextColor(Color.parseColor("#8e665899"));
                        rbNone.setTextColor(Color.parseColor("#8e665899"));
                        break;
                    case R.id.rbEasy:
                        Log.d(TAG, "onCheckedChanged: Easy");
                        notificationStyle = "Easy";
                        rbEasy.setTextColor(Color.parseColor("#be3e82"));
                        rbStrict.setTextColor(Color.parseColor("#8e665899"));
                        rbNone.setTextColor(Color.parseColor("#8e665899"));
                        break;
                    case R.id.rbNone:
                        notificationStyle = "None";
                        rbNone.setTextColor(Color.parseColor("#be3e82"));
                        rbStrict.setTextColor(Color.parseColor("#8e665899"));
                        rbEasy.setTextColor(Color.parseColor("#8e665899"));
                        break;
                }
            }
        });

        //handle the swiping to the next fragment by clicking on the button
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Move to the next fragment

                // Create fragment and pass the selected values as arguments to the next fragment
                GoalOverviewFragment newFragment = GoalOverviewFragment.newInstance(goalType, frequency, monitoringPeriod, idealRangeMin, idealRangeMax, notificationStyle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit);

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });
        return myView;
    }
}
