package com.quattrofolia.balansiosmart.goalList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.models.MonitoringPeriod;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;


public class Timeline extends View {
    private static final String TAG = "Timeline";
    private Paint paint;
    private MonitoringPeriod period;

    public Timeline(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.period = MonitoringPeriod.day;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        postInvalidate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start(); // start the thread to update view gradually
    }

    public void setPeriod(MonitoringPeriod period) {
        this.period = period;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /* Calculate current completion of period */


        DateTime now = new DateTime();
        Interval quantizedInterval = period.quantizedInterval(now.toInstant(), 0);
        Duration duration = quantizedInterval.toDuration();
        DateTime start = quantizedInterval.getStart();
        DateTime end = quantizedInterval.getEnd();
        float past = new Duration(start, now).getMillis();
        float completion = past / duration.getMillis();

        /* Paint the canvas */

        paint = new Paint();
        int width = getWidth();
        int height = 10;
        paint.setStrokeWidth(height);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = height;
        setLayoutParams(params);

        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);

        paint.setColor(ContextCompat.getColor(getContext(), R.color.bs_blank));
        canvas.drawLine(0, height/2, width, height/2, paint);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.bs_primary));
        canvas.drawLine(0, height/2, completion * width, height/2, paint);

    }
}
