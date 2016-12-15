package com.quattrofolia.balansiosmart.goalComposer;

import com.quattrofolia.balansiosmart.models.Discipline;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.HealthDataType;
import com.quattrofolia.balansiosmart.models.MonitoringPeriod;
import com.quattrofolia.balansiosmart.models.NotificationIntensity;
import com.quattrofolia.balansiosmart.models.Range;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/* For defining default assistant settings for
* specific medical conditions */

public enum MedicalCondition {

    DIABETES_TYPE_II("Diabetes, type II") {
        @Override
        public List<Goal> goalPresets() {
            List<Goal> presets = new ArrayList<>();

            HealthDataType bgType = HealthDataType.BLOOD_GLUCOSE;
            Discipline bgDiscipline = new Discipline();
            bgDiscipline.setMonitoringPeriod(MonitoringPeriod.day);
            bgDiscipline.setFrequency(2);
            Range bgRange = new Range();
            bgRange.setLow(new BigDecimal("3.5"));
            bgRange.setHigh(new BigDecimal("8.5"));
            Goal bgGoal = new Goal();
            bgGoal.setType(bgType);
            bgGoal.setDiscipline(bgDiscipline);
            bgGoal.setTargetRange(bgRange);
            bgGoal.setNotificationIntensity(NotificationIntensity.EASY);
            presets.add(bgGoal);

            HealthDataType weightType = HealthDataType.WEIGHT;
            Discipline weightDiscipline = new Discipline();
            weightDiscipline.setMonitoringPeriod(MonitoringPeriod.month);
            weightDiscipline.setFrequency(1);
            Range weightRange = new Range();
            weightRange.setLow(new BigDecimal("60"));
            weightRange.setHigh(new BigDecimal("150"));
            Goal weightGoal = new Goal();
            weightGoal.setType(weightType);
            weightGoal.setDiscipline(weightDiscipline);
            weightGoal.setTargetRange(weightRange);
            weightGoal.setNotificationIntensity(NotificationIntensity.EASY);
            presets.add(weightGoal);

            return presets;
        }
    };

    private void createGoal(HealthDataType type, int frequency, int low, int high, NotificationIntensity notificationIntensity){

    }


    private final String descriptiveName;

    public String getDescriptiveName() {
        return this.descriptiveName;
    }

    MedicalCondition(String descriptiveName) {
        this.descriptiveName = descriptiveName;
    }

    public abstract List<Goal> goalPresets();
}
