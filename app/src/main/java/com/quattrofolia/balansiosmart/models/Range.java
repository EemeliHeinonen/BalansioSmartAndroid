package com.quattrofolia.balansiosmart.models;

import java.math.BigDecimal;

import io.realm.RealmObject;

public class Range extends RealmObject {

    private String low;
    private String high;

    public BigDecimal getLow() {
        return new BigDecimal(low);
    }

    public void setLow(BigDecimal low) {
        this.low = low.toString();
    }

    public BigDecimal getHigh() {
        return new BigDecimal(high);
    }

    public void setHigh(BigDecimal high) {
        this.high = high.toString();
    }


    public boolean contains(BigDecimal number) {
        return (number.compareTo(new BigDecimal(low)) >= 0 && number.compareTo(new BigDecimal(high)) <= 0);
    }
}