package com.quattrofolia.balansiosmart.notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.quattrofolia.balansiosmart.BalansioSmart;
import com.quattrofolia.balansiosmart.ProgressViewActivity;
import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.Session;
import com.quattrofolia.balansiosmart.models.User;
import com.quattrofolia.balansiosmart.storage.Storage;

import org.joda.time.Instant;
import org.joda.time.Minutes;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.RealmList;


/**
 * Created by Mortti on 29.11.2016.
 */

public class NotificationIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    private static String TAG = "jeee";
    private String realmTestString;

    // Storage
    private Realm realm;
    private RealmChangeListener realmChangeListener;
    private Storage storage;
    private RealmList<Goal> goals;




    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
        Log.d(TAG, "NotificationIntentService: ");

    }

    public static Intent createIntentStartNotificationService(Context context) {
        Log.d(TAG, "createIntentStartNotificationService: ");
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Log.d(TAG, "createIntentDeleteNotification: ");
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                Log.d(TAG, "onHandleIntent: actionstart equals action");
                processStartNotification();


            }
            if (ACTION_DELETE.equals(action)) {
                Log.d(TAG, "onHandleIntent: action delete equals action");
                processDeleteNotification(intent);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
            Log.d(TAG, "onHandleIntent: finally");
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
        Log.d(TAG, "processDeleteNotification: ");
    }

    private void processStartNotification() {
        Log.d(TAG, "processStartNotification: ");
        // Do something. For example, fetch fresh data from backend to create a rich notification?

        realm = Realm.getDefaultInstance();
        final Session session = BalansioSmart.currentSession(realm);

        //query.equalTo("name", "John");
        //realmTestString = goals.get(0).getType().toString();
        //Log.d(TAG, "processStartNotification: testString: "+realmTestString);
        final int id = session.getUserId().intValue();
        User managedUser = realm.where(User.class).equalTo("id", id).findFirst();

        //Entry check testing
        Instant lastEntryTime = managedUser.entries.get(managedUser.entries.size()-1).getInstant();
        Log.d(TAG, "processStartNotification: Last Entry time: "+lastEntryTime.getMillis());
        Instant now = new Instant();

        Log.d(TAG, "processStartNotification: present time: "+now);
        Log.d(TAG, "processStartNotification: difference in time(getMillis()-getMillis()): "+(now.getMillis() - lastEntryTime.getMillis()));
        Log.d(TAG, "processStartNotification: difference in time(minutesBetween): "+Minutes.minutesBetween(lastEntryTime, now));


        if (managedUser.entries.size()>5) {
            sendNotification("Scheduled Notification","This notification has been triggered by Notification Service");
        } else {
            sendNotification("Scheduled Notification","there's less than 5 entries");
        }
    }
    private void sendNotification(String title, String text){
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(title)
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentText(text)
                .setSmallIcon(R.drawable.blood_glucose);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                new Intent(this, ProgressViewActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }
}