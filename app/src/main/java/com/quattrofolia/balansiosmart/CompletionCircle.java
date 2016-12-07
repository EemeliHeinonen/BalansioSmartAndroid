package com.quattrofolia.balansiosmart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import static android.graphics.Color.GREEN;
import static android.graphics.Paint.Style.FILL;


public class CompletionCircle extends View {

    private Paint paint;

    public CompletionCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = getWidth();
        int y = getHeight();
        int radius = 100;
        paint.setStyle(FILL);
        paint.setColor(GREEN);
        canvas.drawPaint(paint);
        canvas.drawCircle(x / 2, y / 2, radius, paint);
    }
}
