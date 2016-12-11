package com.quattrofolia.balansiosmart.models;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Discipline extends RealmObject implements Incrementable {

    @PrimaryKey
    private int id;
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

    @Override
    public int getNextPrimaryKey(Realm realm) {
        Number n = realm.where(Discipline.class).max("id");
        if (n != null) {
            return n.intValue() + 1;
        } else {
            return 0;
        }
    }

    @Override
    public void setPrimaryKey(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.setPrimaryKey(id);
    }
}
