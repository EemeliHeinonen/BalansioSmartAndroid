package com.quattrofolia.balansiosmart.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Mortti on 29.11.2016.
 */

public final class NotificationServiceStarterReceiver extends BroadcastReceiver {
    private String TAG = "jeee";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationEventReceiver.setupAlarm(context);
        Log.d(TAG, "onReceive: ");
    }
}