package com.quattrofolia.balansiosmart.models;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

public class Discipline extends RealmObject {

    private int frequency;
    private String monitoringPeriod;

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public MonitoringPeriod getMonitoringPeriod() {
        return MonitoringPeriod.valueOf(monitoringPeriod);
    }

    public void setMonitoringPeriod(MonitoringPeriod monitoringPeriod) {
        this.monitoringPeriod = monitoringPeriod.toString();
    }

    public List<Interval> getSchedule(Instant forInstant, int transposition) {
        List<Interval> intervals = new ArrayList<>();
        Interval quantizedPeriod = getMonitoringPeriod()
                .quantizedInterval(forInstant, transposition);
        Duration schedulePeriod = getMonitoringPeriod()
                .quantizedInterval(forInstant, transposition)
                .toDuration();
        Duration cycle = schedulePeriod.dividedBy(frequency);
        for (int i = 0; i < frequency; i++) {
            Instant start = quantizedPeriod.getStart()
                    .plus(cycle.multipliedBy(i))
                    .toInstant();
            Instant end;
            if (i == frequency - 1) {
                end = quantizedPeriod.getEnd().toInstant();
            } else {
                end = start.plus(cycle);
            }
            intervals.add(new Interval(start, end));
        }
        return intervals;
    }
}
