package com.quattrofolia.balansiosmart.models;

import io.realm.RealmObject;

/**
 * Created by eemeliheinonen on 05/12/2016.
 */

public class RealmBoolean extends RealmObject {
    private Boolean aBoolean;

    public void setaBoolean(Boolean b){
        this.aBoolean = b;
    }

    public Boolean getaBoolean(){
        return this.aBoolean;
    }


}
