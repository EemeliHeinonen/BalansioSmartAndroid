package com.quattrofolia.balansiosmart.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static android.content.ContentValues.TAG;

public  class Discipline extends RealmObject implements AutoIncrementable {

    @PrimaryKey
    private int id;
    private int frequency;
    private String monitoringPeriod;
    private RealmList<RealmBoolean> list ;


    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
        list = new RealmList<>();
        for (int i = 0; i<frequency; i++) {
            RealmBoolean b = new RealmBoolean();
            b.setaBoolean(false);
            this.list.add(b);
        }
        Log.d(TAG, "setFrequency: notificationChecklist size: "+list.size());
    }

    public MonitoringPeriod getMonitoringPeriod() {
        return MonitoringPeriod.valueOf(monitoringPeriod);
    }

    public void setMonitoringPeriod(MonitoringPeriod monitoringPeriod) {
        this.monitoringPeriod = monitoringPeriod.toString();
    }

    @Override
    public void setPrimaryKey(int primaryKey) {
        this.id = primaryKey;
    }

    @Override
    public int getNextPrimaryKey(Realm realm) {
        return realm.where(Discipline.class).max("id").intValue() + 1;
    }
}
