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
import static java.lang.String.format;

/**
 * Created by eemeliheinonen on 27/10/2016.
 */

// Fragment class for selcting progress_view_goal_item_row's range

public class GoalRangeFragment extends Fragment {
    private NumberPicker numberPickerMin;
    private NumberPicker numberPickerMax;
    private int minRangeMin;
    private int minRangeMax;
    private int minRangeDefault;
    private int maxRangeMin;
    private int maxRangeMax;
    private int maxRangeDefault;
    private String minSelectedValue;
    private String maxSelectedValue;
    private int userWeight;
    private String goalType;
    private int frequency;
    private String monitoringPeriod;
    private Button btnNext;
    private Button btnSkip;
    private TextView tvRangeMin;
    private TextView tvRangeMax;
    private String[] minValues = new String[8];
    private String[] maxValues = new String[8];




    public static GoalRangeFragment newInstance(String goalType, int frequency, String monitoringPeriod) {
        GoalRangeFragment fragment = new GoalRangeFragment();
        Bundle args = new Bundle();
        args.putString("goalType", goalType);
        args.putInt("frequency", frequency);
        args.putString("monitoringPeriod", monitoringPeriod);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userWeight = 70;
        minRangeMin = 50;
        minRangeMax = 150;
        minRangeDefault = 80;
        minSelectedValue = Integer.toString(minRangeDefault);
        maxRangeMin = 50;
        maxRangeMax = 150;
        maxRangeDefault = 120;
        maxSelectedValue = Integer.toString(maxRangeDefault);

        //get data from the previous fragments
        if (getArguments() != null) {
            goalType = getArguments().getString("goalType");
            frequency = getArguments().getInt("frequency");
            monitoringPeriod = getArguments().getString("monitoringPeriod");
            Log.d(TAG, "onCreate: goalType is "+goalType);
            Log.d(TAG, "onCreate: frequency is "+frequency);
            Log.d(TAG, "onCreate: monitoringPeriod is "+monitoringPeriod);
        } else {
            Log.d(TAG, "onCreate: arguments null");
        }
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout myView =(RelativeLayout) inflater.inflate(R.layout.goal_range_fragment, container, false);
        tvRangeMin = (TextView) myView.findViewById(R.id.textViewGoalRangeMin);
        tvRangeMax = (TextView) myView.findViewById(R.id.textViewGoalRangeMax);
        numberPickerMin = (NumberPicker) myView.findViewById(R.id.numberPicker_min);
        numberPickerMax = (NumberPicker) myView.findViewById(R.id.numberPicker_max);
        btnNext = (Button) myView.findViewById(R.id.btnRangeNext);
        btnSkip = (Button) myView.findViewById(R.id.btnRangeSkip);

        // Initialize the pickers
        numberPickerMin.setMaxValue(minRangeMax);
        numberPickerMin.setMinValue(minRangeMin);
        numberPickerMin.setValue(minRangeDefault);
        numberPickerMin.setWrapSelectorWheel(false);
        numberPickerMax.setMaxValue(maxRangeMax);
        numberPickerMax.setMinValue(maxRangeMin);
        numberPickerMax.setValue(maxRangeDefault);
        numberPickerMax.setWrapSelectorWheel(false);

        //check if a certain progress_view_goal_item_row type has been selected & modify the fragment accordingly
        if (goalType.equals("Weight")) {
            weightMode();
        } else if(goalType.equals("Sleep")) {
            sleepMode();
        } else if(goalType.equals("Blood Pressure Systolic")) {
            bpSystolicMode();
        } else if(goalType.equals("Blood Pressure Diastolic")) {
            bpDiastolicMode();
        } else if(goalType.equals("Blood Glucose")) {
            bgMode();
        }

        numberPickerMin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Check data type and display the newly selected number from picker
                if (goalType.equals("Sleep")){
                    Log.d(TAG, "onValueChange: min: "+newVal);
                    minSelectedValue = Integer.toString(newVal);
                    maxSelectedValue = Integer.toString(newVal);
                } else if (goalType.equals("Blood Glucose")){
                    Log.d(TAG, "onValueChange: "+minValues[newVal]);
                    minSelectedValue = minValues[newVal];
                } else {
                    Log.d(TAG, "onValueChange: min: "+newVal);
                    minSelectedValue = Integer.toString(newVal);
                }
            }
        });

        numberPickerMax.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Check data type and display the newly selected number from picker
                if (goalType.equals("Blood Glucose")){
                    Log.d(TAG, "onValueChange: String "+maxValues[newVal]);
                    maxSelectedValue = maxValues[newVal];
                } else {
                    Log.d(TAG, "onValueChange: max: " + newVal);
                    maxSelectedValue = Integer.toString(newVal);
                }
            }
        });

        //handle the swiping to the next fragment by clicking on the button
        btnSkip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Move to the next fragment without passing new data from this fragment

                GoalNotificationFragment newFragment = GoalNotificationFragment.newInstance(goalType, frequency, monitoringPeriod, "0", "0");
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Move to the next fragment

                // Create fragment and pass the selected values as arguments to the next fragment
                GoalNotificationFragment newFragment = GoalNotificationFragment.newInstance(goalType, frequency, monitoringPeriod, minSelectedValue, maxSelectedValue);
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

    //Methods for initializing the fragment for different progress_view_goal_item_row types.
    public void weightMode(){
        int defaultMin = userWeight-5;
        int defaultMax = userWeight+5;
        minSelectedValue = Integer.toString(defaultMin);
        maxSelectedValue = Integer.toString(defaultMax);
        numberPickerMin.setMinValue(userWeight-10);
        numberPickerMin.setMaxValue(userWeight);
        numberPickerMin.setValue(defaultMin);
        numberPickerMax.setMinValue(userWeight);
        numberPickerMax.setMaxValue(userWeight+10);
        numberPickerMax.setValue(defaultMax);
        Log.d(TAG, "weightMode: called");
    }

    public void sleepMode(){
        numberPickerMin.setMinValue(4);
        numberPickerMin.setMaxValue(9);
        numberPickerMin.setValue(8);
        minSelectedValue = "8";
        maxSelectedValue = "8";
        numberPickerMin.setWrapSelectorWheel(false);
        numberPickerMax.setVisibility(View.GONE);
        tvRangeMax.setVisibility(View.GONE);
        tvRangeMin.setText("How many hours should you try to sleep a night?");
        //numberPickerMin.setPaddingRelative(0,300,0,0);
    }

    public void bpSystolicMode(){
        numberPickerMin.setMinValue(90);
        numberPickerMin.setMaxValue(110);
        numberPickerMax.setMinValue(140);
        numberPickerMax.setMaxValue(160);
        numberPickerMin.setValue(100);
        numberPickerMax.setValue(150);
        minSelectedValue = "100";
        maxSelectedValue = "150";
    }

    public void bpDiastolicMode(){
        numberPickerMin.setMinValue(60);
        numberPickerMin.setMaxValue(80);
        numberPickerMax.setMinValue(80);
        numberPickerMax.setMaxValue(100);
        numberPickerMin.setValue(70);
        numberPickerMax.setValue(90);
        minSelectedValue = "70";
        maxSelectedValue = "90";
    }

    public void bgMode(){
        Log.d(TAG, "bgMode: called");
        double minNum = 3;
        double maxNum = 6;

        //Loop for populating the pickers with numbers that have decimals
        for (int i = 0; i< minValues.length; i++) {
            minNum += 0.5;
            maxNum += 0.5;
            String number = format("%.1f", minNum);
            String maxNumber = format("%.1f", maxNum);
            Log.d(TAG, "bgMode: number: "+number);
            minValues[i] = number;
            maxValues[i] = maxNumber;
            Log.d(TAG, "bgMode: minVal i: "+minValues[i]);
            Log.d(TAG, "bgMode: maxVal i: "+maxValues[i]);
        }

        numberPickerMin.setMaxValue(minValues.length-1);
        numberPickerMin.setMinValue(0);
        numberPickerMin.setValue(0);
        numberPickerMin.setDisplayedValues(minValues);
        numberPickerMax.setMaxValue(maxValues.length-1);
        numberPickerMax.setMinValue(0);
        numberPickerMax.setValue(0);
        numberPickerMax.setDisplayedValues(maxValues);
    }
}