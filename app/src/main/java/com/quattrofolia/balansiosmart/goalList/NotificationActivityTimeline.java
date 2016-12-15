package com.quattrofolia.balansiosmart.goalList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.models.NotificationEntry;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivityTimeline extends Timeline {

    private static final String TAG = "NotificationActivityTim";

    private float diam;
    private List<NotificationEntry> activity;

    public NotificationActivityTimeline(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = new ArrayList<>();
        diam = getResources().getDimension(R.dimen.timeline_item);
    }

    public void setActivity(List<NotificationEntry> activity) {
        this.activity = activity;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        for (NotificationEntry e : activity) {
            if (getPeriod().quantizedInterval(getNow().toInstant(), 0).contains(e.getInstant())) {

                float position = positionInPeriod(e.getInstant(), getPeriod());
                Log.d(TAG, "position: " + position);
                Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_action_name);
                float fractionWidth = position * getWidth();
                int delta = Math.round(fractionWidth);
                d.setBounds(-30, 0 + delta, 30, 60 + delta);
                d.draw(canvas);

            }

        }
    }
}