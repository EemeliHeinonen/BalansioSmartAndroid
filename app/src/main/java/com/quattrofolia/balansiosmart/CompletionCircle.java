package com.quattrofolia.balansiosmart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


public class CompletionCircle extends View {

    private Paint paint;

    public CompletionCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int radius = getHeight()/2;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.GREEN);
        canvas.drawPaint(paint);
        canvas.drawCircle(radius, radius, radius, paint);
    }
}
