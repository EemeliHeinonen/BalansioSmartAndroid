package com.quattrofolia.balansiosmart.goalComposer;

import com.quattrofolia.balansiosmart.models.HealthDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrbeva on 10/30/16.
 */

// Class for handling the data that will be shown in the recyclerView of the GoalTypeFragment

public class GoalTypeListData {

    private static final String[] headers = {"Blood Glucose", "Blood Pressure Systolic", "Blood Pressure Diastolic", "Weight", "Exercise", "Sleep", "Nutrition"};
    private static final HealthDataType[] types = {
            HealthDataType.BLOOD_GLUCOSE,
            HealthDataType.BLOOD_PRESSURE_SYSTOLIC,
            HealthDataType.BLOOD_PRESSURE_DIASTOLIC,
            HealthDataType.WEIGHT,
            HealthDataType.EXERCISE,
            HealthDataType.SLEEP,
            HealthDataType.NUTRITION
    };

    public static List<GoalTypeListItem> getListData() {
        List<GoalTypeListItem> itemList = new ArrayList<>();
        for (HealthDataType dt : types) {
            GoalTypeListItem listItem = new GoalTypeListItem(dt);
            itemList.add(listItem);
        }
        return itemList;
    }

}
