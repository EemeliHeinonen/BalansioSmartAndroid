package com.quattrofolia.balansiosmart.goalComposer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.models.Discipline;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.Range;

import java.math.BigDecimal;


import static android.content.ContentValues.TAG;
import static com.quattrofolia.balansiosmart.models.HealthDataType.BLOOD_GLUCOSE;
import static com.quattrofolia.balansiosmart.models.HealthDataType.BLOOD_PRESSURE_DIASTOLIC;
import static com.quattrofolia.balansiosmart.models.HealthDataType.BLOOD_PRESSURE_SYSTOLIC;
import static com.quattrofolia.balansiosmart.models.HealthDataType.EXERCISE;
import static com.quattrofolia.balansiosmart.models.HealthDataType.NUTRITION;
import static com.quattrofolia.balansiosmart.models.HealthDataType.SLEEP;
import static com.quattrofolia.balansiosmart.models.HealthDataType.WEIGHT;
import static com.quattrofolia.balansiosmart.models.MonitoringPeriod.day;
import static com.quattrofolia.balansiosmart.models.MonitoringPeriod.month;
import static com.quattrofolia.balansiosmart.models.MonitoringPeriod.week;

/**
 * Created by eemeliheinonen on 09/11/2016.
 */


// Fragment class for showing the created progress_view_goal_item_row and adding it to model

public class GoalOverviewFragment extends Fragment {

    private String goalType;
    private int frequency;
    private String monitoringPeriod;
    private String idealRangeMin;
    private String idealRangeMax;
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
            notificationStyle = getArguments().getString("notificationStyle");

            // Create a goal object and give it a Discipline and a Range object,
            // if the user selected not to skip their values in the GoalComposer
            goal = new Goal();
            goal.setNotificationStyle(notificationStyle);
            if(frequency!=0) {
                discipline = new Discipline();
            }
            if (!idealRangeMax.equals("0")){
                range = new Range();
            }
        } else {
            Log.d(TAG, "onCreate: arguments null");
        }


        //Set the correct HealthDataType for the goal object
        if (goalType.equals("Weight")) {
            goal.setType(WEIGHT);
        } else if (goalType.equals("Blood Pressure Systolic")) {
            goal.setType(BLOOD_PRESSURE_SYSTOLIC);
        } else if (goalType.equals("Blood Pressure Diastolic")) {
            goal.setType(BLOOD_PRESSURE_DIASTOLIC);
        } else if (goalType.equals("Blood Glucose")) {
            goal.setType(BLOOD_GLUCOSE);
        } else if (goalType.equals("Exercise")) {
            goal.setType(EXERCISE);
        } else if (goalType.equals("Sleep")) {
            goal.setType(SLEEP);
        } else if (goalType.equals("Nutrition")) {
            goal.setType(NUTRITION);
        }


        //Set values for Discipline and Range objects, in case they're not null
        if (discipline!=null) {
            discipline.setFrequency(frequency);
            if (monitoringPeriod.equals("day")) {
                discipline.setMonitoringPeriod(day);
            } else if (monitoringPeriod.equals("week")) {
                discipline.setMonitoringPeriod(week);
            } else {
                discipline.setMonitoringPeriod(month);
            }
            goal.setDiscipline(discipline);
        }

        if (range!=null) {
            range.setLow(new BigDecimal(idealRangeMin));
            range.setHigh(new BigDecimal(idealRangeMax));
            goal.setTargetRange(range);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.goal_overview_fragment, container, false);
        TextView tvType = (TextView) myView.findViewById(R.id.tvOverviewType);
        TextView tvFrequency = (TextView) myView.findViewById(R.id.tvOverviewFrequency);
        TextView tvRangeMin = (TextView) myView.findViewById(R.id.tvOverviewRangeMin);
        TextView tvRangeMax = (TextView) myView.findViewById(R.id.tvOverviewRangeMax);
        Button btnOverviewFinish = (Button) myView.findViewById(R.id.btnOverviewFinish);
        Button btnOverviewAnother = (Button) myView.findViewById(R.id.btnOverviewAnother);

        tvType.setText("Goal type: "+goalType);
        tvFrequency.setText(frequency+" Measurement(s) a "+monitoringPeriod);
        if (!idealRangeMin.equals("0") && !idealRangeMax.equals("0")){
            tvRangeMin.setText("Goal range minimum value: "+idealRangeMin);
            tvRangeMax.setText("Goal range maximum value: "+idealRangeMax);
        }

        //Check if the user is editing an existing goal object or creating a new one
        if (((GoalComposerActivity) getActivity()).isEditingGoal()) {
            btnOverviewAnother.setVisibility(View.INVISIBLE);
            btnOverviewFinish.setText(R.string.button_text_edit_goal);

            btnOverviewFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((GoalComposerActivity) getActivity()).editGoal(goal);
                }
            });
        } else {
            btnOverviewAnother.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Add the newly created progress_view_goal_item_row object to the users list of goals
                    // and return to the GoalTypeFragment to create another goal
                    ((GoalComposerActivity) getActivity()).addGoal(goal, false);
                    GoalTypeFragment newFragment = GoalTypeFragment.newInstance();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

            btnOverviewFinish.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Add the newly created progress_view_goal_item_row object to the users list of goals
                    ((GoalComposerActivity) getActivity()).addGoal(goal, true);
                }
            });
        }

        return myView;
    }
}
