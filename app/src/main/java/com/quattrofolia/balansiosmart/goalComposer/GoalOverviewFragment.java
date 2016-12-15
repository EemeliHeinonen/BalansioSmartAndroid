package com.quattrofolia.balansiosmart.goalComposer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.goalDetails.Pair;
import com.quattrofolia.balansiosmart.goalDetails.ValuePairViewAdapter;
import com.quattrofolia.balansiosmart.models.Discipline;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.HealthDataType;
import com.quattrofolia.balansiosmart.models.MonitoringPeriod;
import com.quattrofolia.balansiosmart.models.Range;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by eemeliheinonen on 09/11/2016.
 */


// Fragment class for showing the created progress_view_goal_item_row and adding it to model

public class GoalOverviewFragment extends Fragment {


    /* Current setup view */
    private List<Pair<String, String>> goalSettings;
    private TextView goalTypeHeader;
    ValuePairViewAdapter goalSettingsAdapter;
    RecyclerView goalSettingsView;


    private HealthDataType dataType;
    private int frequency;
    private MonitoringPeriod monitoringPeriod;
    private String idealRangeMin;
    private String idealRangeMax;
    private String notificationStyle;
    private Goal goal;
    private Discipline discipline;
    private Range range;

    public static GoalOverviewFragment newInstance(Bundle args) {
        GoalOverviewFragment fragment = new GoalOverviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static GoalOverviewFragment newInstance(
            HealthDataType dataType, int frequency, MonitoringPeriod monitoringPeriod,
            String idealRangeMin, String idealRangeMax, String notificationStyle) {
        GoalOverviewFragment fragment = new GoalOverviewFragment();
        Bundle args = new Bundle();
        args.putString("goalType", dataType.toString());
        args.putInt("frequency", frequency);
        if (monitoringPeriod != null) {
            args.putString("monitoringPeriod", monitoringPeriod.toString());
        }
        args.putString("rangeMin", idealRangeMin);
        args.putString("rangeMax", idealRangeMax);
        args.putString("notificationStyle", notificationStyle);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (goal != null) {
            Log.d(TAG, goal.getType().getLongName());
        }

        //get data from the previous fragments
        if (getArguments() != null) {
            dataType = HealthDataType.valueOf(getArguments().getString("goalType"));
            frequency = getArguments().getInt("frequency");
            String p = getArguments().getString("monitoringPeriod");
            if (p != null) {
                monitoringPeriod = MonitoringPeriod.valueOf(p);
            }
            idealRangeMin = getArguments().getString("rangeMin");
            idealRangeMax = getArguments().getString("rangeMax");
            notificationStyle = getArguments().getString("notificationStyle");

            // Create a goal object and give it a Discipline and a Range object,
            // if the user selected not to skip their values in the GoalComposer
            goal = new Goal();
            goal.setNotificationStyle(notificationStyle);
            if (frequency != 0) {
                discipline = new Discipline();
            }
            if (!idealRangeMax.equals("0")) {
                range = new Range();
            }
        } else {
            Log.d(TAG, "onCreate: arguments null");
        }


        //Set the correct HealthDataType for the goal object
        goal.setType(dataType);

        //Set values for Discipline and Range objects, in case they're not null
        if (discipline != null) {
            discipline.setFrequency(frequency);
            discipline.setMonitoringPeriod(monitoringPeriod);
            goal.setDiscipline(discipline);
        }

        if (range != null) {
            range.setLow(new BigDecimal(idealRangeMin));
            range.setHigh(new BigDecimal(idealRangeMax));
            goal.setTargetRange(range);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.goal_overview_fragment, container, false);
        TextView tvType = (TextView) myView.findViewById(R.id.tvOverviewType);
        TextView tvFrequency = (TextView) myView.findViewById(R.id.tvOverviewFrequency);
        TextView tvRangeMin = (TextView) myView.findViewById(R.id.tvOverviewRangeMin);
        TextView tvRangeMax = (TextView) myView.findViewById(R.id.tvOverviewRangeMax);
        Button btnOverviewFinish = (Button) myView.findViewById(R.id.btnOverviewFinish);
        Button btnOverviewAnother = (Button) myView.findViewById(R.id.btnOverviewAnother);

        tvType.setText("Goal type: " + dataType.getLongName());
        if (monitoringPeriod != null) {
            tvFrequency.setText(frequency + " Measurement(s) a " + monitoringPeriod.name());
        }
        if (!idealRangeMin.equals("0") && !idealRangeMax.equals("0")) {
            tvRangeMin.setText("Goal range minimum value: " + idealRangeMin);
            tvRangeMax.setText("Goal range maximum value: " + idealRangeMax);
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


        goalSettingsView = (RecyclerView) myView.findViewById(R.id.recyclerView_goalSettings);
        goalSettings = new ArrayList<>();
        goalSettingsAdapter = new ValuePairViewAdapter(goalSettings);
        goalSettingsView.setAdapter(goalSettingsAdapter);
        goalSettingsView.setLayoutManager(
                new LinearLayoutManager(myView.getContext(), LinearLayoutManager.VERTICAL, false)
        );
        goalTypeHeader = (TextView) myView.findViewById(R.id.textView_goalTypeHeader);
        goalTypeHeader.setText(dataType.getLongName());


        return myView;
    }
}
