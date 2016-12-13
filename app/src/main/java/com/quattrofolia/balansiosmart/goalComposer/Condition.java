package com.quattrofolia.balansiosmart.goalComposer;

import com.quattrofolia.balansiosmart.models.Discipline;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.MonitoringPeriod;
import com.quattrofolia.balansiosmart.models.Range;

import java.math.BigDecimal;

public enum Condition {

    diabetes_type_i("Diabetes, type I") {
        public Goal assistantSetup() {
            Discipline discipline = new Discipline();
            discipline.setMonitoringPeriod(MonitoringPeriod.day);
            discipline.setFrequency(6);
            Range range = new Range();
            range.setLow(new BigDecimal("3.5"));
            range.setHigh(new BigDecimal("5.5"));
            Goal goal = new Goal();
            goal.setTargetRange(range);
            return new Goal();
        }
    },

    diabetes_type_ii("Diabetes, type II") {
        public Goal assistantSetup() {
            Discipline discipline = new Discipline();
            discipline.setMonitoringPeriod(MonitoringPeriod.week);
            discipline.setFrequency(7);
            Range range = new Range();
            range.setLow(new BigDecimal("3.5"));
            range.setHigh(new BigDecimal("8.5"));
            Goal goal = new Goal();
            goal.setTargetRange(range);
            return new Goal();
        }
    };


    private final String descriptiveName;

    public String getDescriptiveName() {
        return this.descriptiveName;
    }

    Condition(String descriptiveName) {
        this.descriptiveName = descriptiveName;
    }
}
