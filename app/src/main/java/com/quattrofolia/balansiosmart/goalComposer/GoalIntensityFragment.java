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
import com.quattrofolia.balansiosmart.models.HealthDataType;
import com.quattrofolia.balansiosmart.models.MonitoringPeriod;

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
    private MonitoringPeriod monitoringPeriod;
    private NumberPicker npMonitoringPeriod;
    private NumberPicker npFrequency;
    private final MonitoringPeriod[] periods = {
            MonitoringPeriod.day,
            MonitoringPeriod.week,
            MonitoringPeriod.month
    };
    private HealthDataType dataType;

    public static GoalIntensityFragment newInstance(HealthDataType dataType) {
        GoalIntensityFragment fragment = new GoalIntensityFragment();
        Bundle args = new Bundle();
        args.putString("dataType", dataType.toString());
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Init default periods
        frequencyMin = 1;
        frequencyMax = 10;
        frequencyDefault = 5;
        periodMin = 0;
        periodMax = periods.length - 1;
        periodDefault = 0;
        selectedFrequency = frequencyDefault;
        monitoringPeriod = periods[0];

        //get data from the previous fragment
        if (getArguments() != null) {
            dataType = HealthDataType.valueOf(getArguments().getString("dataType"));
        } else {
            Log.d(TAG, "onCreate: arguments null");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.goal_intensity_fragment, container, false);
        tvFrequency = (TextView) myView.findViewById(R.id.textViewGoalIntensity);
        tvMonitoringPeriod = (TextView) myView.findViewById(R.id.textViewGoalIntensityDesc);
        btnNext = (Button) myView.findViewById(R.id.btnIntensityNext);
        btnSkip = (Button) myView.findViewById(R.id.btnIntensitySkip);
        npFrequency = (NumberPicker) myView.findViewById(R.id.npGoalIntensityAmount);
        npMonitoringPeriod = (NumberPicker) myView.findViewById(R.id.npGoalIntensityTime);
        tvFrequency.setText("Select ");

        //Initialize the NumberPickers
        npFrequency.setMinValue(frequencyMin);
        npFrequency.setMaxValue(frequencyMax);
        npFrequency.setValue(frequencyDefault);
        npFrequency.setWrapSelectorWheel(false);

        String[] periodStrings = new String[periods.length];
        for (int i = 0; i < periodStrings.length; i++) {
            periodStrings[i] = periods[i].toString();
        }
        npMonitoringPeriod.setDisplayedValues(periodStrings);
        npMonitoringPeriod.setMinValue(periodMin);
        npMonitoringPeriod.setMaxValue(periodMax);
        npMonitoringPeriod.setValue(periodDefault);

        //Set a value change listener for amount NumberPicker
        npFrequency.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Set the selected value to a variable
                selectedFrequency = newVal;
            }
        });

        //Set a value change listener for time frame NumberPicker
        npMonitoringPeriod.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Set the selected value to a variable
                monitoringPeriod = periods[newVal];
            }
        });

        //check if a certain progress_view_goal_item_row type has been selected & modify the fragment accordingly

        switch (dataType) {
            case WEIGHT:
                weightMode();
                break;
            case BLOOD_GLUCOSE:
                bgMode();
                break;
            case EXERCISE:
                exerciseMode();
                break;
            default:
                break;
        }

        //handle the swiping to the next fragment by clicking on the button
        btnSkip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Move to the next fragment without passing new data from this fragment
                GoalRangeFragment newFragment = GoalRangeFragment.newInstance(dataType, 0, null);
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
                // depending on which dataType has been selected

                Fragment fragment;
                FragmentTransaction transaction;

                switch (dataType) {
                    case EXERCISE:
                        fragment = GoalNotificationFragment.newInstance(dataType, selectedFrequency, monitoringPeriod, "0", "0");
                        break;
                    default:
                        fragment = GoalRangeFragment.newInstance(dataType, selectedFrequency, monitoringPeriod);
                        break;
                }

                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return myView;
    }

    //Methods for initializing the fragment for different progress_view_goal_item_row types.
    public void weightMode() {
        int weightDefaultFrequency = 2;
        npFrequency.setMinValue(1);
        npFrequency.setMaxValue(10);
        npFrequency.setValue(weightDefaultFrequency);
        selectedFrequency = weightDefaultFrequency;
        monitoringPeriod = periods[0];
    }

    public void bgMode() {
        monitoringPeriod = periods[0];
        npMonitoringPeriod.setVisibility(View.GONE);
        tvMonitoringPeriod.setVisibility(View.GONE);
        tvFrequency.setText("Select the number of " + monitoringPeriod.getDescriptiveName() + " measurements");
    }

    public void exerciseMode() {
        monitoringPeriod = periods[1];
        npMonitoringPeriod.setVisibility(View.GONE);
        tvMonitoringPeriod.setVisibility(View.GONE);
        tvFrequency.setText("Times of exercise a week");
    }
}
