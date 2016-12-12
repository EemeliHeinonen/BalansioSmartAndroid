package com.quattrofolia.balansiosmart.goalList;

import android.view.animation.Animation;
import android.view.animation.Transformation;


public class CircleAnimation extends Animation {
    private PeriodTimer circle;
    private float oldAngle;
    private float newAngle;

    public CircleAnimation(PeriodTimer circle, float newAngle) {
        this.oldAngle = circle.getSweepAngle();
        this.newAngle = newAngle;
        this.circle = circle;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float angle = oldAngle + ((newAngle - oldAngle) * interpolatedTime);
        circle.setSweepAngle(angle);
        circle.requestLayout();
    }
}
