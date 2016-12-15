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
import org.joda.time.Instant;
import org.joda.time.Interval;


public class Timeline extends View {
    private static final String TAG = "Timeline";
    private Paint paint;
    private MonitoringPeriod period;

    public DateTime getNow() {
        return now;
    }

    private DateTime now;

    public Timeline(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.period = MonitoringPeriod.day;
        this.now = new DateTime();
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

    public MonitoringPeriod getPeriod() {
        return this.period;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /* Calculate current completion of period */

        now.toDate();
        Interval quantizedInterval = period.quantizedInterval(now.toInstant(), 0);
        float completion = instantToPeriodRatio(now.toInstant());

        /* Paint the canvas */

        paint = new Paint();
        int width = getWidth();
        int height = 50;
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

    public float instantToPeriodRatio(Instant instant) {
        DateTime dt = instant.toDateTime();
        Interval i = period.quantizedInterval(now.toInstant(), 0);
        Duration duration = i.toDuration();
        DateTime start = i.getStart();
        float past = new Duration(start, dt).getMillis();
        return past / duration.getMillis();
    }
}
