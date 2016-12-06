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
import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import com.quattrofolia.balansiosmart.models.Session;
import com.quattrofolia.balansiosmart.models.User;
import com.quattrofolia.balansiosmart.storage.Storage;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Minutes;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;




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


    private RealmResults<Goal> allGoals;
    private Goal weightGoal;
    private Goal bgGoal;
    private Goal bpsGoal;
    private Goal bpdGoal;
    private Goal sleepGoal;
    private Goal exerciseGoal;
    private Goal nutritionGoal;

    private RealmResults<HealthDataEntry> allEntries;
    private RealmResults<HealthDataEntry> weightEntries;
    private RealmResults<HealthDataEntry> bgEntries;
    private RealmResults<HealthDataEntry> bpsEntries;
    private RealmResults<HealthDataEntry> bpdEntries;
    private RealmResults<HealthDataEntry> sleepEntries;
    private RealmResults<HealthDataEntry> exerciseEntries;
    private RealmResults<HealthDataEntry> nutritionEntries;





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
        final int id = session.getUserId().intValue();

        User managedUser = realm.where(User.class).equalTo("id", id).findFirst();


        initGoals();
        initEntries();
        easyDisciplineCheck();
        Log.d(TAG, "processStartNotification: allGoals size: "+allGoals.size());
        Log.d(TAG, "processStartNotification: last goal's type"+allGoals.get(allGoals.size()-1).getType().getLongName());
        Log.d(TAG, "processStartNotification: weightGoal TEST: "+weightGoal.getType().getLongName());
        Log.d(TAG, "processStartNotification: bgGoal TEST: "+bgGoal.getType().getLongName());

        //Entry check testing
        Instant lastEntryTime = managedUser.entries.get(managedUser.entries.size()-1).getInstant();
        Log.d(TAG, "processStartNotification: isToday: "+isToday(lastEntryTime.toDateTime()));
        Log.d(TAG, "processStartNotification: Last Entry time: "+lastEntryTime);
        Instant now = new Instant();
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


    private void initGoals(){
        allGoals = realm.where(Goal.class).findAll();
        weightGoal = realm.where(Goal.class).equalTo("type", "WEIGHT").findFirst();
        bgGoal = realm.where(Goal.class).equalTo("type", "BLOOD_GLUCOSE").findFirst();
        bpsGoal = realm.where(Goal.class).equalTo("type", "BLOOD_PRESSURE_SYSTOLIC").findFirst();
        bpdGoal = realm.where(Goal.class).equalTo("type", "BLOOD_PRESSURE_DIASTOLIC").findFirst();
        sleepGoal = realm.where(Goal.class).equalTo("type", "SLEEP").findFirst();
        exerciseGoal = realm.where(Goal.class).equalTo("type", "EXERCISE").findFirst();
        nutritionGoal = realm.where(Goal.class).equalTo("type", "NUTRITION").findFirst();
    }

    private void initEntries(){
        allEntries = realm.where(HealthDataEntry.class).findAll();
        bgEntries = realm.where(HealthDataEntry.class).equalTo("type", "BLOOD_GLUCOSE").findAll();
        weightEntries = realm.where(HealthDataEntry.class).equalTo("type", "WEIGHT").findAll();
        bpsEntries = realm.where(HealthDataEntry.class).equalTo("type", "BLOOD_PRESSURE_SYSTOLIC").findAll();
        bpdEntries = realm.where(HealthDataEntry.class).equalTo("type", "BLOOD_PRESSURE_DIASTOLIC").findAll();
        sleepEntries = realm.where(HealthDataEntry.class).equalTo("type", "SLEEP").findAll();
        exerciseEntries = realm.where(HealthDataEntry.class).equalTo("type", "EXERCISE").findAll();
        nutritionEntries = realm.where(HealthDataEntry.class).equalTo("type", "NUTRITION").findAll();
    }

    private void easyDisciplineCheck(){
        // TODO: check that goals monitoringperiod is day
        RealmResults<Goal> easyDisciplineGoals = allGoals.where().equalTo("notificationStyle", "Easy").isNotNull("discipline").findAll();
        if (easyDisciplineGoals.size()>0) {
            Log.d(TAG, "easyDisciplineCheck: easyDiscipline Typen Test log: "+easyDisciplineGoals.first().getType().toString());
            int entryIsTodayCounter = 0;

            for (int i = 0; i < easyDisciplineGoals.size(); i++){
                RealmResults<HealthDataEntry> currentEasyDiscliplineEntries = allEntries.where().equalTo("type", easyDisciplineGoals.get(i).getType().toString()).findAll();
                 for (int j = 0; j < currentEasyDiscliplineEntries.size(); j++ ) {
                     if (isToday(currentEasyDiscliplineEntries.get(j).getInstant().toDateTime())){
                         entryIsTodayCounter++;
                     }
                 }

            }

            //Log.d(TAG, "easyDisciplineCheck: number of entrys of the first item in the easyDisciplineGoals list: "+ i);
            Log.d(TAG, "easyDisciplineCheck: easyDisciplineGoals Size: "+easyDisciplineGoals.size());
            Log.d(TAG, "easyDisciplineCheck: vikan easy itemin type: "+easyDisciplineGoals.get(easyDisciplineGoals.size()-1).getType().getLongName());
            Log.d(TAG, "easyDisciplineCheck: vikan easy itemin discipline: "+easyDisciplineGoals.get(easyDisciplineGoals.size()-1).getDiscipline().toString());
        } else {
            Log.d(TAG, "easyDisciplineCheck: easyDisciplineGoals is empty");
        }
    }

    private boolean isToday(DateTime dt){
        DateTime midnightToday = DateTime.now().withTimeAtStartOfDay();
        if (dt.isAfter(midnightToday)){
            return true;
        } else {
            return false;
        }
    }
}