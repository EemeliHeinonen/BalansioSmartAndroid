package com.quattrofolia.balansiosmart.goalComposer;

import com.quattrofolia.balansiosmart.models.HealthDataType;

/**
 * Created by mrbeva on 10/30/16.
 */

//part of GoalTypeFragment

public class GoalTypeListItem {

    private String header;

    private HealthDataType dataType;

    public GoalTypeListItem(HealthDataType dataType) {
        this.dataType = dataType;
    }

    public String getHeader() {
        return header;
    }

    public HealthDataType getDataType() {
        return dataType;
    }

    public void setDataType(HealthDataType dataType) {
        this.dataType = dataType;
    }
}
