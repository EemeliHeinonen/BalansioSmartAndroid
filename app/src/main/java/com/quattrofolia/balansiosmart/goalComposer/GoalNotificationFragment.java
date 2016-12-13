package com.quattrofolia.balansiosmart.goalComposer;

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

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.models.HealthDataType;
import com.quattrofolia.balansiosmart.models.MonitoringPeriod;

import static android.content.ContentValues.TAG;

/**
 * Created by eemeliheinonen on 27/10/2016.
 */

// Fragment class for selecting progress_view_goal_item_row's notification preferences

public class GoalNotificationFragment extends Fragment {

    private HealthDataType dataType;
    private int frequency;
    private MonitoringPeriod monitoringPeriod;
    private String idealRangeMin;
    private String idealRangeMax;
    RadioGroup radioButtonGroup;
    RadioButton rbStrict;
    RadioButton rbEasy;
    RadioButton rbNone;
    private String notificationStyle = "Strict";

    public static GoalNotificationFragment newInstance
            (HealthDataType dataType, int frequency, MonitoringPeriod monitoringPeriod, String idealRangeMin, String idealRangeMax) {
        GoalNotificationFragment fragment = new GoalNotificationFragment();
        Bundle args = new Bundle();
        args.putString("goalType", dataType.toString());
        args.putInt("frequency", frequency);
        args.putString("monitoringPeriod", monitoringPeriod.toString());
        args.putString("rangeMin", idealRangeMin);
        args.putString("rangeMax", idealRangeMax);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get data from the previous fragments
        if (getArguments() != null) {
            dataType = HealthDataType.valueOf(getArguments().getString("goalType"));
            frequency = getArguments().getInt("frequency");
            monitoringPeriod = MonitoringPeriod.valueOf(getArguments().getString("monitoringPeriod"));
            idealRangeMin = getArguments().getString("rangeMin");
            idealRangeMax = getArguments().getString("rangeMax");
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
        tv.setText("Remind me to measure "+dataType.getLongName());

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
                        break;

                    case R.id.rbEasy:
                        Log.d(TAG, "onCheckedChanged: Easy");
                        notificationStyle = "Easy";
                        break;

                    case R.id.rbNone:
                        notificationStyle = "None";
                        break;
                }
            }
        });

        //handle the navigation and data passing to the next fragment by clicking on the button
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Create fragment and pass the selected values as arguments to the next fragment
                GoalOverviewFragment newFragment = GoalOverviewFragment.newInstance(dataType, frequency, monitoringPeriod, idealRangeMin, idealRangeMax, notificationStyle);
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
