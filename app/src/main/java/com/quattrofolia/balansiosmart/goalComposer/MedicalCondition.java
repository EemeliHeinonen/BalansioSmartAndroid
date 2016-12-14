package com.quattrofolia.balansiosmart.goalComposer;

import com.quattrofolia.balansiosmart.models.Discipline;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.HealthDataType;
import com.quattrofolia.balansiosmart.models.MonitoringPeriod;
import com.quattrofolia.balansiosmart.models.NotificationIntensity;
import com.quattrofolia.balansiosmart.models.Range;

import java.math.BigDecimal;

public enum MedicalCondition {

    diabetes_type_i("Diabetes, type I") {
        @Override
        public Goal goalPreset() {
            HealthDataType type = HealthDataType.BLOOD_GLUCOSE;
            Discipline discipline = new Discipline();
            discipline.setMonitoringPeriod(MonitoringPeriod.day);
            discipline.setFrequency(6);
            Range range = new Range();
            range.setLow(new BigDecimal("3.5"));
            range.setHigh(new BigDecimal("5.5"));
            Goal goal = new Goal();
            goal.setType(type);
            goal.setDiscipline(discipline);
            goal.setTargetRange(range);
            goal.setNotificationIntensity(NotificationIntensity.STRICT);
            return goal;
        }
    },

    diabetes_type_ii("Diabetes, type II") {
        @Override
        public Goal goalPreset() {
            HealthDataType type = HealthDataType.BLOOD_GLUCOSE;
            Discipline discipline = new Discipline();
            discipline.setMonitoringPeriod(MonitoringPeriod.week);
            discipline.setFrequency(7);
            Range range = new Range();
            range.setLow(new BigDecimal("3.5"));
            range.setHigh(new BigDecimal("8.5"));
            Goal goal = new Goal();
            goal.setType(type);
            goal.setDiscipline(discipline);
            goal.setTargetRange(range);
            goal.setNotificationIntensity(NotificationIntensity.EASY);
            return goal;
        }
    };


    private final String descriptiveName;

    public String getDescriptiveName() {
        return this.descriptiveName;
    }

    MedicalCondition(String descriptiveName) {
        this.descriptiveName = descriptiveName;
    }

    public abstract Goal goalPreset();
}
