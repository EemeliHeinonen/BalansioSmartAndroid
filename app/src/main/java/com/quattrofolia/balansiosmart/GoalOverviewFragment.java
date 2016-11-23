package com.quattrofolia.balansiosmart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quattrofolia.balansiosmart.models.Discipline;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.Range;

import java.math.BigDecimal;


import static android.content.ContentValues.TAG;
import static com.quattrofolia.balansiosmart.models.HealthDataType.BLOOD_PRESSURE_DIASTOLIC;
import static com.quattrofolia.balansiosmart.models.HealthDataType.BLOOD_PRESSURE_SYSTOLIC;
import static com.quattrofolia.balansiosmart.models.HealthDataType.WEIGHT;
import static com.quattrofolia.balansiosmart.models.MonitoringPeriod.day;
import static com.quattrofolia.balansiosmart.models.MonitoringPeriod.month;
import static com.quattrofolia.balansiosmart.models.MonitoringPeriod.week;

/**
 * Created by eemeliheinonen on 09/11/2016.
 */


// Fragment class for showing the created goal and adding it to model

public class GoalOverviewFragment extends Fragment {

    private String goalType;
    private int frequency;
    private String monitoringPeriod;
    private String idealRangeMin;
    private String idealRangeMax;
    private String notficationStyle;
    private Goal goal;
    private Discipline discipline;
    private Range range;

    public static GoalOverviewFragment newInstance(
            String GoalType, int frequency, String monitoringPeriod,
            String idealRangeMin, String idealRangeMax, String notificationStyle) {
        GoalOverviewFragment fragment = new GoalOverviewFragment();
        Bundle args = new Bundle();
        args.putString("goalType", GoalType);
        args.putInt("frequency", frequency);
        args.putString("monitoringPeriod", monitoringPeriod);
        args.putString("rangeMin", idealRangeMin);
        args.putString("rangeMax", idealRangeMax);
        args.putString("notificationStyle", notificationStyle);
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
            notficationStyle = getArguments().getString("notificationStyle");

            goal = new Goal();
            if(frequency!=0) {
                discipline = new Discipline();
            }
            if (!idealRangeMax.equals("0")){
                range = new Range();
            }

            Log.d(TAG, "onCreate: goaltype: "+goalType);
            Log.d(TAG, "onCreate: measurement frequency: "+frequency);
            Log.d(TAG, "onCreate: monitoringPeriod: "+monitoringPeriod);
            Log.d(TAG, "onCreate: ideal range minimum value: "+idealRangeMin);
            Log.d(TAG, "onCreate: ideal range maximum value: "+idealRangeMax);
            Log.d(TAG, "onCreate: notification Style: "+notficationStyle);
        } else {
            Log.d(TAG, "onCreate: arguments null");
        }
        if (goalType.equals("Weight")) {
            goal.setType(WEIGHT);
            Log.d(TAG, "onCreate: Goal print" + goal.toString());
        } else if (goalType.equals("Blood Pressure Systolic")) {
            goal.setType(BLOOD_PRESSURE_SYSTOLIC);
        } else if (goalType.equals("Blood Pressure Diastolic")) {
            goal.setType(BLOOD_PRESSURE_DIASTOLIC);
        }

        if (discipline!=null) {
            discipline.setFrequency(frequency);
            if (monitoringPeriod.equals("day")) {
                discipline.setMonitoringPeriod(day);
                Log.d(TAG, "onCreate: discipline getMonitoringPeriod: " + discipline.getMonitoringPeriod().toString());

            } else if (monitoringPeriod.equals("week")) {
                discipline.setMonitoringPeriod(week);
                Log.d(TAG, "onCreate: discipline getMonitoringPeriod: " + discipline.getMonitoringPeriod().toString());

            } else {
                discipline.setMonitoringPeriod(month);
                Log.d(TAG, "onCreate: discipline getMonitoringPeriod: " + discipline.getMonitoringPeriod().toString());
            }
            goal.setDiscipline(discipline);
        }

        if (range!=null) {
            range.setLow(new BigDecimal(idealRangeMin));
            Log.d(TAG, "onCreate:range getLow:  " + range.getLow());

            range.setHigh(new BigDecimal(idealRangeMax));
            Log.d(TAG, "onCreate: range getHigh: " + range.getHigh());

            goal.setTargetRange(range);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.goal_overview_fragment, container, false);
        TextView tvType = (TextView) myView.findViewById(R.id.tvOverviewType);
        TextView tvFrequency = (TextView) myView.findViewById(R.id.tvOverviewFrequency);
        TextView tvRangeMin = (TextView) myView.findViewById(R.id.tvOverviewRangeMin);
        TextView tvRangeMax = (TextView) myView.findViewById(R.id.tvOverviewRangeMax);
        Button btnCreateGoal = (Button) myView.findViewById(R.id.btnCreateGoal);

        tvType.setText("Goal type: "+goalType);
        tvFrequency.setText(frequency+" Measurement(s) a "+monitoringPeriod);
        if (!idealRangeMin.equals("0") && !idealRangeMax.equals("0")){
            tvRangeMin.setText("Goal range minimum value: "+idealRangeMin);
            tvRangeMax.setText("Goal range maximum value: "+idealRangeMax);
        }

        btnCreateGoal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Add the newly created goal object to the users list of goals
                ((GoalComposerActivity) getActivity()).addGoal(goal);
            }
        });

        return myView;
    }
}
