package com.quattrofolia.balansiosmart.goalComposer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quattrofolia.balansiosmart.R;

import static android.content.ContentValues.TAG;

/**
 * Created by eemeliheinonen on 27/10/2016.
 */

// Fragment class for selecting progress_view_goal_item_row's intensity
public class GoalIntensityFragment extends Fragment {
    private int frequencyMin;
    private int frequencyMax;
    private int periodMin;
    private int periodMax;
    private int frequencyDefault;
    private int periodDefault;
    private TextView tvFrequency;
    private TextView tvMonitoringPeriod;
    Button btnNext;
    Button btnSkip;
    private int selectedFrequency;
    private String selectedMonitoringPeriod;
    private NumberPicker npMonitoringPeriod;
    private NumberPicker npFrequency;
    private final String[] values = {"day","week", "month"};
    private String goalType;

    public static GoalIntensityFragment newInstance(String goalType) {
        GoalIntensityFragment fragment = new GoalIntensityFragment();
        Bundle args = new Bundle();
        args.putString("goalType", goalType);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Init default values
        frequencyMin = 1;
        frequencyMax = 10;
        frequencyDefault = 5;
        periodMin = 0;
        periodMax = values.length-1;
        periodDefault = 0;
        selectedFrequency = frequencyDefault;
        selectedMonitoringPeriod = values[0];

        //get data from the previous fragment
        if (getArguments() != null) {
            goalType = getArguments().getString("goalType");
        } else {
            Log.d(TAG, "onCreate: arguments null");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout myView =(RelativeLayout) inflater.inflate(R.layout.goal_intensity_fragment, container, false);
        tvFrequency = (TextView) myView.findViewById(R.id.textViewGoalIntensity);
        tvMonitoringPeriod = (TextView) myView.findViewById(R.id.textViewGoalIntensityDesc);
        btnNext = (Button) myView.findViewById(R.id.btnIntensityNext);
        btnSkip = (Button) myView.findViewById(R.id.btnIntensitySkip);
        npFrequency = (NumberPicker) myView.findViewById(R.id.npGoalIntensityAmount);
        npMonitoringPeriod = (NumberPicker) myView.findViewById(R.id.npGoalIntensityTime);
        tvFrequency.setText("Number of measurements");

        //Initialize the NumberPickers
        npFrequency.setMinValue(frequencyMin);
        npFrequency.setMaxValue(frequencyMax);
        npFrequency.setValue(frequencyDefault);
        npFrequency.setWrapSelectorWheel(false);
        npMonitoringPeriod.setDisplayedValues(values);
        npMonitoringPeriod.setMinValue(periodMin);
        npMonitoringPeriod.setMaxValue(periodMax);
        npMonitoringPeriod.setValue(periodDefault);

        //Set a value change listener for amount NumberPicker
        npFrequency.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Set the selected value to a variable
                selectedFrequency = newVal;}
        });

        //Set a value change listener for time frame NumberPicker
        npMonitoringPeriod.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Set the selected value to a variable
                selectedMonitoringPeriod = values[newVal];
            }
        });

        //check if a certain progress_view_goal_item_row type has been selected & modify the fragment accordingly
        if (goalType.equals("Weight")) {
            weightMode();
        } else if(goalType.equals("Blood Glucose")) {
            bgMode();
        } else if (goalType.equals("Exercise")) {
            exerciseMode();
        }

        //handle the swiping to the next fragment by clicking on the button
        btnSkip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Move to the next fragment without passing new data from this fragment
                GoalRangeFragment newFragment = GoalRangeFragment.newInstance(goalType, 0, "none");
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //handle the navigation and data passing to the next fragment by clicking on the button,
                // depending on which goalType has been selected
                if (goalType.equals("Exercise")) {
                    // Create fragment and pass the selected values as arguments to the next fragment
                    GoalNotificationFragment newFragment = GoalNotificationFragment.newInstance(goalType, selectedFrequency, selectedMonitoringPeriod, "0", "0");
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter, R.anim.exit);

                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack so the user can navigate back
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();
                } else {
                    GoalRangeFragment newFragment = GoalRangeFragment.newInstance(goalType, selectedFrequency, selectedMonitoringPeriod);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });
        return myView;
    }

    //Methods for initializing the fragment for different progress_view_goal_item_row types.
    public void weightMode(){
        int weightDefaultFrequency = 2;
        npFrequency.setMinValue(1);
        npFrequency.setMaxValue(10);
        npFrequency.setValue(weightDefaultFrequency);
        selectedFrequency = weightDefaultFrequency;
        selectedMonitoringPeriod = values[0];
    }

    public void bgMode(){
        selectedMonitoringPeriod = values[0];
        npMonitoringPeriod.setVisibility(View.GONE);
        tvMonitoringPeriod.setVisibility(View.GONE);
        tvFrequency.setText("Number of measurements a day");
        tvFrequency.setPaddingRelative(0,300,0,0);
    }


    public void exerciseMode(){
        selectedMonitoringPeriod = values[1];
        npMonitoringPeriod.setVisibility(View.GONE);
        tvMonitoringPeriod.setVisibility(View.GONE);
        tvFrequency.setText("Times of exercise a week");
        tvFrequency.setPaddingRelative(0,300,0,0);
    }
}
