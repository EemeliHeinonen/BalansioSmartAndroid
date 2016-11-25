package com.quattrofolia.balansiosmart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrbeva on 10/30/16.
 */

// Class for handling the data that will be shown in the recyclerView of the GoalTypeFragment

public class GoalTypeListData {

    private static final String[] headers = {"Blood Glucose", "Blood Pressure Systolic", "Blood Pressure Diastolic", "Weight", "Exercise", "Sleep", "Nutrition"};

    public static List<GoalTypeListItem> getListData() {
        List<GoalTypeListItem> goal_composer_type = new ArrayList<>();


        for (int i = 0; i < headers.length; i++) {
            GoalTypeListItem listItem = new GoalTypeListItem();
            listItem.setHeader(headers[i]);
            goal_composer_type.add(listItem);
        }
        return goal_composer_type;
    }
}
