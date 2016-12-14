package com.quattrofolia.balansiosmart.models;

import java.math.BigDecimal;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Range extends RealmObject {

    /* Range object defines the target value range for a single Goal object. */

    @Required
    private String low;
    @Required
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

    public String getDescriptiveName() {
        return "" + getLow() + "-" + getHigh();
    }
}