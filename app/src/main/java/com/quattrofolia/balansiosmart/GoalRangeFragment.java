package com.quattrofolia.balansiosmart;

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

import static android.content.ContentValues.TAG;

/**
 * Created by eemeliheinonen on 27/10/2016.
 */

public class GoalRangeFragment extends Fragment {
    private NumberPicker npMin;
    private NumberPicker npMax;
    private int minRangeMin;
    private int minRangeMax;
    private int minRangeDefault;
    private int maxRangeMin;
    private int maxRangeMax;
    private int maxRangeDefault;
    private int minSelectedValue;
    private int maxSelectedValue;
    private int userWeight;
    private String goalType;
    private int measurementAmount = 0;
    private String timeframe;
    private Button btn;
    //private static final String ARG_TYPE = "txt";
    //private static final String ARG_AMOUNT = "txt";
    private static final String ARG_TIMEFRAME = "txt";


    public static GoalRangeFragment newInstance(String goalType, int measurementAmount, String timeframe) {
        GoalRangeFragment fragment = new GoalRangeFragment();
        Bundle args = new Bundle();
        args.putString("goalType", goalType);
        args.putInt("intAmount", measurementAmount);
        args.putString(ARG_TIMEFRAME, timeframe);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            goalType = getArguments().getString("goalType");
            measurementAmount = getArguments().getInt("intAmount");
            timeframe = getArguments().getString(ARG_TIMEFRAME);
            Log.d(TAG, "onCreate: goalType is "+goalType);
            Log.d(TAG, "onCreate: measurementAmount is "+measurementAmount);
            Log.d(TAG, "onCreate: timeframe is "+timeframe);
        } else {
            Log.d(TAG, "onCreate: arguments null");
        }

        userWeight = 70;
        minRangeMin = 50;
        minRangeMax = 150;
        minRangeDefault = 80;
        minSelectedValue = minRangeDefault;
        maxRangeMin = 50;
        maxRangeMax = 150;
        maxRangeDefault = 120;
        maxSelectedValue = maxRangeDefault;
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout myView =(RelativeLayout) inflater.inflate(R.layout.goal_range_fragment, container, false);
        TextView tv = (TextView) myView.findViewById(R.id.textViewGoalRange);
        npMin = (NumberPicker) myView.findViewById(R.id.numberPicker_min);
        npMax = (NumberPicker) myView.findViewById(R.id.numberPicker_max);
        btn = (Button) myView.findViewById(R.id.btnRangeNext);
        tv.setText("This is the goal ideal input range fragment");

        // picker values are set according to the type

        npMin.setMaxValue(minRangeMax);
        npMin.setMinValue(minRangeMin);
        npMin.setValue(minRangeDefault);
        npMax.setMaxValue(maxRangeMax);
        npMax.setMinValue(maxRangeMin);
        npMax.setValue(maxRangeDefault);


        if (goalType.equals("Weight")) {
            weightMode();
        }


        npMin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                Log.d(TAG, "onValueChange: min: "+newVal);
                minSelectedValue = newVal;
            }
        });

        npMax.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                Log.d(TAG, "onValueChange: max: "+newVal);
                maxSelectedValue = newVal;
            }
        });

        //handle the swiping to the next fragment by clicking on the button
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Move to the next fragment

                // Create fragment and give it an argument specifying the article it should show
                GoalNotificationFragment newFragment = GoalNotificationFragment.newInstance(goalType, measurementAmount, timeframe, minSelectedValue, maxSelectedValue);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

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

    public void weightMode(){
        npMin.setMinValue(userWeight-10);
        npMin.setMaxValue(userWeight);
        npMax.setMinValue(userWeight);
        npMax.setMaxValue(userWeight+10);
        Log.d(TAG, "weightMode: called");
    }
}
