package com.quattrofolia.balansiosmart.cardstack.animation;

import android.animation.TypeEvaluator;
import android.widget.RelativeLayout.LayoutParams;

import com.quattrofolia.balansiosmart.cardstack.CardUtils;


//Part of ProgressView's CardView
public class RelativeLayoutParamsEvaluator implements TypeEvaluator<LayoutParams> {

    @Override
    public LayoutParams evaluate(float fraction, LayoutParams start,
                                 LayoutParams end) {

        LayoutParams result = CardUtils.cloneParams(start);
        result.leftMargin += ((end.leftMargin-start.leftMargin)*fraction);
        result.rightMargin += ((end.rightMargin-start.rightMargin)*fraction);
        result.topMargin += ((end.topMargin-start.topMargin)*fraction);
        result.bottomMargin += ((end.bottomMargin-start.bottomMargin)*fraction);
        return result;
    }

}