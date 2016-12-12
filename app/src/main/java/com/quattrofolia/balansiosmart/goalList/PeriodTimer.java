package com.quattrofolia.balansiosmart.goalList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.models.Discipline;
import com.quattrofolia.balansiosmart.models.Goal;

import org.joda.time.DateTime;
import org.joda.time.Duration;


public class PeriodTimer extends View {
    private final static String TAG = "PeriodTimer";
    private Goal goal;
    private float startAngle;
    private float sweepAngle;

    public PeriodTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                        postInvalidate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
        startAngle = 270;
        sweepAngle = startAngle;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float strokeWidth = 6;
        float radius = width / 2 - strokeWidth / 2;
        float center = radius + (strokeWidth / 2);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        Discipline discipline = this.goal.getDiscipline();
        if (discipline == null) {
            return;
        }
        Duration duration = new Duration(60000);
        DateTime now = new DateTime().withMillisOfSecond(0);
        DateTime start = new DateTime(
                now.getYear(),
                now.getMonthOfYear(),
                now.getDayOfMonth(),
                now.getHourOfDay(),
                now.getMinuteOfHour(),
                0,
                0);
        DateTime end = start.plus(duration);
        float past = new Duration(start, now).getMillis();
        float completion = past / duration.getMillis();
        // Log.d(TAG, "now: " + now.getMillis() + "past: " + past + ", future: " + future + ", completion: " + completion);


        paint.setColor(ContextCompat.getColor(getContext(), R.color.bs_blank));
        canvas.drawCircle(center, center, radius, paint);

        float arcRectStarting = center - radius;
        float arcRectEnding = center + radius;
        final RectF oval = new RectF();
        oval.set(arcRectStarting, arcRectStarting, arcRectEnding, arcRectEnding);
        // CircleAnimation animation = new CircleAnimation(this, sweepAngle);
        //animation.setDuration(1000);
        // this.startAnimation(animation);
        this.sweepAngle = completion * 360;
        paint.setColor(ContextCompat.getColor(getContext(), R.color.bs_primary));
        canvas.drawArc(oval, startAngle, sweepAngle, false, paint);


    }

    public float getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(float angle) {
        this.sweepAngle = angle;
    }
}
