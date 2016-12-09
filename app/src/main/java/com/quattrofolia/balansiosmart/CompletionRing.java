package com.quattrofolia.balansiosmart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import static android.graphics.Paint.Style.STROKE;


public class CompletionRing extends View {

    private static final String TAG = "CompletionCircle";
    private Paint paint;
    private float width;
    private float strokeWidth;
    private float radius;
    private float completion;
    private boolean enabled;

    public CompletionRing(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(STROKE);
        strokeWidth = 5;
        paint.setStrokeWidth(strokeWidth);
        completion = 0;
        enabled = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        radius = width / 2 - strokeWidth / 2;
        float centerX = radius + (strokeWidth /2);
        float centerY = radius + (strokeWidth /2);

        if (enabled) {

            paint.setColor(ContextCompat.getColor(getContext(), R.color.testRed));
            canvas.drawCircle(centerX, centerY, radius, paint);

            float arcRectStartingX = centerX - radius;
            float arcRectStartingY = centerY - radius;
            float arcRectEndingX = centerX + radius;
            float arcRectEndingY = centerY + radius;
            final RectF oval = new RectF();
            oval.set(arcRectStartingX, arcRectStartingY, arcRectEndingX, arcRectEndingY);

            float startAngle = 270;
            float sweepAngle = completion * 360;

            paint.setColor(ContextCompat.getColor(getContext(), R.color.testBlue));
            canvas.drawArc(oval, startAngle, sweepAngle, false, paint);
        } else {
            paint.setColor(Color.GRAY);
            canvas.drawCircle(centerX, centerY, radius, paint);
        }
    }

    public void setCompletion(float completion) {
        this.completion = completion;
        this.invalidate();
    }

    public void disable() {
        paint.setColor(Color.GRAY);
        this.invalidate();
    }
}