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

            presets.add(createGoal(HealthDataType.BLOOD_GLUCOSE,MonitoringPeriod.day,2,"3.5","7",NotificationIntensity.NONE));
            presets.add(createGoal(HealthDataType.WEIGHT,MonitoringPeriod.week,3,"65","75",NotificationIntensity.EASY));
            presets.add(createGoal(HealthDataType.EXERCISE,MonitoringPeriod.day,2,NotificationIntensity.EASY));
            presets.add(createGoal(HealthDataType.SLEEP,MonitoringPeriod.day,1,"8","8",NotificationIntensity.NONE));

            //presets.add(createGoal(HealthDataType.BLOOD_PRESSURE_DIASTOLIC,MonitoringPeriod.day,2,"3.5","7",NotificationIntensity.NONE));
            //presets.add(createGoal(HealthDataType.BLOOD_PRESSURE_SYSTOLIC,MonitoringPeriod.day,2,"3.5","7",NotificationIntensity.NONE));

            return presets;
        }
        private Goal createGoal(HealthDataType type,MonitoringPeriod moped, int frequency, String low, String high, NotificationIntensity notificationIntensity){
            HealthDataType bgType = type;
            Discipline bgDiscipline = new Discipline();
            bgDiscipline.setMonitoringPeriod(moped);
            bgDiscipline.setFrequency(frequency);
            Range bgRange = new Range();
            bgRange.setLow(new BigDecimal(low));
            bgRange.setHigh(new BigDecimal(high));
            Goal bgGoal = new Goal();
            bgGoal.setType(bgType);
            bgGoal.setDiscipline(bgDiscipline);
            bgGoal.setTargetRange(bgRange);
            bgGoal.setNotificationIntensity(notificationIntensity);
            return bgGoal;
        }
        private Goal createGoal(HealthDataType type,MonitoringPeriod moped, int frequency, NotificationIntensity notificationIntensity){
            HealthDataType bgType = type;
            Discipline bgDiscipline = new Discipline();
            bgDiscipline.setMonitoringPeriod(moped);
            bgDiscipline.setFrequency(frequency);
            Goal bgGoal = new Goal();
            bgGoal.setType(bgType);
            bgGoal.setDiscipline(bgDiscipline);
            bgGoal.setNotificationIntensity(notificationIntensity);
            return bgGoal;
        }

    };




    private final String descriptiveName;

    public String getDescriptiveName() {
        return this.descriptiveName;
    }

    MedicalCondition(String descriptiveName) {
        this.descriptiveName = descriptiveName;
    }

    public abstract List<Goal> goalPresets();
}
