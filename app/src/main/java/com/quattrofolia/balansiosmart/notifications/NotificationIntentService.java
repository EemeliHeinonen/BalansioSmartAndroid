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
import com.quattrofolia.balansiosmart.goalComposer.GoalComposerActivity;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import com.quattrofolia.balansiosmart.models.HealthDataType;
import com.quattrofolia.balansiosmart.models.NotificationEntry;
import com.quattrofolia.balansiosmart.models.Session;
import com.quattrofolia.balansiosmart.storage.Storage;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;

import java.math.BigDecimal;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


/**
 * Created by Mortti on 29.11.2016.
 */

public class NotificationIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final int GOAL_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    private static final String ACTION_REMOVE_GOAL_NOTIFICATIONS = "ACTION_REMOVE_GOAL_NOTIFICATIONS";
    private static String TAG = "theTAG";

    // Storage
    private Realm realm;
    private RealmChangeListener realmChangeListener;
    private Storage storage;

    private Instant now;

    private RealmResults<Goal> allGoals;
    private Duration twoHours;

    private RealmResults<NotificationEntry> allNotificationEntries;
    private RealmResults<NotificationEntry> easyDisciplineNotificationEntries;
    private RealmResults<HealthDataEntry> allEntries;



    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
            if (ACTION_DELETE.equals(action)) {
                processDeleteNotification(intent);
            }
            if (ACTION_REMOVE_GOAL_NOTIFICATIONS.equals(action)) {
                removeGoalNotifications(intent.getIntExtra("notificationId", 0), (HealthDataType) intent.getSerializableExtra("type"));

            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
    }

    //sets goals notification mode to no notifications.
    private void removeGoalNotifications(int notificationId, HealthDataType goalType) {
        // TODO: get goal and change notificationstyle, and store with storage
        Log.d(TAG, "removeGoalNotifications: for " + goalType);
        realm = Realm.getDefaultInstance();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (realm != null) {
            realm.beginTransaction();
            Goal targetGoal = realm.where(Goal.class).equalTo("type", goalType.name()).findFirst();
            if (targetGoal != null) {
                Log.d(TAG, "removeGoalNotifications: targetGoal: " + targetGoal.getNotificationStyle());
                targetGoal.setNotificationStyle("none");
            } else {
                Log.d(TAG, "removeGoalNotifications: TargetGoal is null");
            }
            //storage.save(targetGoal);
            Log.d(TAG, "removeGoalNotifications: targetGoal saved, with new notificationStyle: "+targetGoal.getNotificationStyle());
            realm.commitTransaction();
            manager.cancel(notificationId);
        } else {
            Log.d(TAG, "removeGoalNotifications: realm is null");
        }
    }

    //main method for initialization and starting notification logic.
    private void processStartNotification() {
        Log.d(TAG, "processStartNotification: ");
        // Do something. For example, fetch fresh data from backend to create a rich notification?
        now = new Instant();
        realm = Realm.getDefaultInstance();
        final Session session = BalansioSmart.currentSession(realm);
        storage = new Storage();
        twoHours = Duration.standardHours(2);

        initGoals();
        initEntries();
        initNotificationEntries();
        easyDisciplineCheck();
        easyClinicalCheck();




    }

    //method for creating the actual notification with set parameters
    private void sendNotification(String title, String text, HealthDataType goalType) {
        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        //goalComposerIntent needs to use the HealthDataType's getLongName, whereas the removeNotificationsIntent needs the all caps version.

        PendingIntent progressViewIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                new Intent(this, ProgressViewActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent goalComposerIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                new Intent(this, GoalComposerActivity.class)
                        .putExtra("type", goalType)
                        .putExtra("notificationId", NOTIFICATION_ID)
                        .putExtra("goalId", GOAL_ID),
                        PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent removeNotificationsIntent = PendingIntent.getService(this,
                NOTIFICATION_ID,
                new Intent(this, NotificationIntentService.class)
                        .putExtra("type", goalType)
                        .putExtra("notificationId", NOTIFICATION_ID)
                        .setAction(ACTION_REMOVE_GOAL_NOTIFICATIONS),
                        PendingIntent.FLAG_UPDATE_CURRENT);


        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(title)
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.bs_primary))
                .setContentText(text)
                .setSmallIcon(R.drawable.bg)
                .addAction(R.drawable.ic_watch_later_black_18dp, "later", progressViewIntent)
                .addAction(R.drawable.ic_create_black_18dp, "edit", goalComposerIntent).setAutoCancel(true)
                .addAction(R.drawable.ic_do_not_disturb_black_18dp, "disable notifications", removeNotificationsIntent).setAutoCancel(true);


        builder.setContentIntent(progressViewIntent);
        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));


        manager.notify(NOTIFICATION_ID, builder.build());
    }

    //initializing goals from Realm
    private void initGoals() {
        allGoals = realm.where(Goal.class).findAll();
    }

    //initializing entries from Realm
    private void initEntries() {
        allEntries = realm.where(HealthDataEntry.class).findAll();
    }

    //initializing notification entries (=previously sent notifications) from Realm
    private void initNotificationEntries() {
        allNotificationEntries = realm.where(NotificationEntry.class).findAll();
        easyDisciplineNotificationEntries = realm.where(NotificationEntry.class).equalTo("value", "easyDiscipline").findAll();

    }



    //check if discipline goals with easy notification setting need to send notifications
    private void easyDisciplineCheck() {
        RealmResults<Goal> easyDisciplineGoals = allGoals.where().equalTo("notificationStyle", "Easy").isNotNull("discipline").findAll();
        if (easyDisciplineGoals.size() > 0) {
            Log.d(TAG, "easyDisciplineCheck: easyDiscipline Typen Test log: " + easyDisciplineGoals.first().getType().toString());

            for (int i = 0; i < easyDisciplineGoals.size(); i++) {
                if (isLastHourOfMonitoringPeriod(easyDisciplineGoals.get(i).getDiscipline().getMonitoringPeriod().quantizedInterval(now, 0).getEnd())) {
                    RealmResults<HealthDataEntry> currentEasyDiscliplineEntries = allEntries.where().equalTo("type", easyDisciplineGoals.get(i)
                            .getType().toString()).findAll();
                    int entryIsInPeriodCounter = 0;
                    String currentMonitoringPeriod = easyDisciplineGoals.get(i).getDiscipline().getMonitoringPeriod().toString();
                    Log.d(TAG, "easyDisciplineCheck: monitoringPeriod: " + currentMonitoringPeriod);
                    for (int j = 0; j < currentEasyDiscliplineEntries.size(); j++) {

                        if (isToday(currentEasyDiscliplineEntries.get(j).getInstant().toDateTime()) && currentMonitoringPeriod.equals("day")) {
                            entryIsInPeriodCounter++;
                        }
                        if (isThisWeek(currentEasyDiscliplineEntries.get(j).getInstant().toDateTime()) && currentMonitoringPeriod.equals("week")) {
                            entryIsInPeriodCounter++;
                        }
                        if (isThisMonth(currentEasyDiscliplineEntries.get(j).getInstant().toDateTime()) && currentMonitoringPeriod.equals("month")) {
                            entryIsInPeriodCounter++;
                        }
                    }
                    Log.d(TAG, "easyDisciplineCheck: easydisciplinenotificationentries size: " + easyDisciplineNotificationEntries.size());
                    RealmResults<NotificationEntry> currentNotificationEntries = allNotificationEntries.where()
                            .equalTo("type", easyDisciplineGoals
                                    .get(i)
                                    .getType()
                                    .toString()).equalTo("value", "easyDiscipline").findAll();

                    if (easyDisciplineGoals.get(i).getDiscipline().getFrequency() > entryIsInPeriodCounter) {
                        if (currentNotificationEntries.isEmpty()) {

                            Log.d(TAG, "easyDisciplineCheck: goal failed");
                            writeNotificationEntry(easyDisciplineGoals.get(i).getType(), "easyDiscipline",easyDisciplineGoals.get(i).getType().getLongName()+" discipline goal did fail.");
                            sendNotification(easyDisciplineGoals.get(i).getType().getLongName() + "goal has failed", "You didn't accomplish your goal this time", easyDisciplineGoals.get(i).getType());
                        } else if (currentNotificationEntries
                                .last().getInstant().isBefore(now.minus(twoHours))) {

                            Log.d(TAG, "easyDisciplineCheck: goal failed");
                            writeNotificationEntry(easyDisciplineGoals.get(i).getType(), "easyDiscipline",easyDisciplineGoals.get(i).getType().getLongName()+" discipline goal did fail.");
                            sendNotification(easyDisciplineGoals.get(i).getType().getLongName() + "goal has failed", "You didn't accomplish your goal this time", easyDisciplineGoals.get(i).getType());
                        }
                    } else {
                        Log.d(TAG, "easyDisciplineCheck: goal accomplished / has already been notified about");
                    }
                }
            }
        } else {
            Log.d(TAG, "easyDisciplineCheck: easyDisciplineGoals is empty");
        }
    }

    //check if clinical goals with easy notification setting need to send notifications
    private void easyClinicalCheck() {

        if (isWakingHours()) {
            RealmResults<Goal> easyClinicalGoals = allGoals.where().equalTo("notificationStyle", "Easy").isNotNull("targetRange").findAll();
            if (easyClinicalGoals.size() > 0) {
                Log.d(TAG, "easyClinicalCheck: easyClinical Typen Test log: " + easyClinicalGoals.first().getType().toString());
                for (Goal goal : easyClinicalGoals) {

                    int numberOfFailedEntries = 0;
                    RealmResults<HealthDataEntry> currentEasyClinicalEntries = allEntries.where().equalTo("type", goal
                            .getType().toString()).findAll();
                    RealmResults<NotificationEntry> currentNotificationEntries = allNotificationEntries.where().equalTo("type", goal.getType().toString()).equalTo("value", "easyClinical").findAll();
                    if (currentEasyClinicalEntries.size() > 4) {
                        if (currentNotificationEntries.isEmpty()) {
                            Log.d(TAG, "easyClinicalCheck: currentNotificationEntries" + currentNotificationEntries.size());
                            BigDecimal currentGoalMinRange = goal.getTargetRange().getLow();
                            BigDecimal currentGoalMaxRange = goal.getTargetRange().getHigh();

                            for (int i = currentEasyClinicalEntries.size() - 1; i >= currentEasyClinicalEntries.size() - 5; i--) {
                                BigDecimal currentValue = new BigDecimal(currentEasyClinicalEntries.get(i).getValue());

                                if (currentValue.compareTo(currentGoalMinRange) < 0 || currentValue.compareTo(currentGoalMaxRange) > 0) {
                                    numberOfFailedEntries++;
                                }
                            }
                        } else if (currentNotificationEntries.last().getInstant().isBefore(currentEasyClinicalEntries.get(currentEasyClinicalEntries.size() - 5).getInstant())) {
                            BigDecimal currentGoalMinRange = goal.getTargetRange().getLow();
                            BigDecimal currentGoalMaxRange = goal.getTargetRange().getHigh();

                            for (int i = currentEasyClinicalEntries.size() - 1; i >= currentEasyClinicalEntries.size() - 5; i--) {
                                BigDecimal currentValue = new BigDecimal(currentEasyClinicalEntries.get(i).getValue());

                                if (currentValue.compareTo(currentGoalMinRange) < 0 || currentValue.compareTo(currentGoalMaxRange) > 0) {
                                    numberOfFailedEntries++;
                                }
                            }
                        }
                    }
                    if (numberOfFailedEntries >= 5) {
                        writeNotificationEntry(goal.getType(), "easyClinical",goal.getType().getLongName() + " clinical goal did fail");
                        sendNotification(goal.getType().getLongName() + " clinical goal has failed", "You didn't accomplish your goal this time", goal.getType());
                    }
                    else{
                        Log.d(TAG, "easyClinicalCheck: " + goal.getType().getLongName() + " clinical goal accomplished.");
                    }
                }
            } else {
                Log.d(TAG, "easyClinicalCheck: easyClinicalGoals is empty");
            }
        }
    }


    private boolean isToday(DateTime dt) {
        DateTime midnightToday = now.toDateTime().withTimeAtStartOfDay();
        if (dt.isAfter(midnightToday)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isThisWeek(DateTime dt) {
        DateTime.Property thisWeek = now.toDateTime().weekOfWeekyear();
        DateTime.Property thisYear = now.toDateTime().year();
        if (dt.weekOfWeekyear().getAsText().equals(thisWeek.getAsText()) && dt.year().getAsText().equals(thisYear.getAsText())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isThisMonth(DateTime dt) {
        DateTime.Property thisMonth = now.toDateTime().monthOfYear();
        DateTime.Property thisYear = now.toDateTime().year();
        if (dt.monthOfYear().getAsText().equals(thisMonth.getAsText()) && dt.year().getAsText().equals(thisYear.getAsText())) {
            return true;
        } else {
            return false;
        }
    }

    //returns true if currently is the last hour a monitoring period
    private boolean isLastHourOfMonitoringPeriod(DateTime dt) {
        Interval window = new Interval(dt.minusHours(15), dt.minusHours(1));
        if (window.contains(now)) {
            return true;
        } else {
            return false;
        }

    }

    //returns true if currently is tha waking hours (8-22)
    private boolean isWakingHours() {
        Interval wakingHours = new Interval(now.toDateTime().withHourOfDay(8).withMinuteOfHour(0), now.toDateTime().withHourOfDay(22).withMinuteOfHour(0));
        if (wakingHours.contains(now)) {
            return true;
        } else {
            return false;
        }
    }

    //create notification entry to the model, to keep track of what notifications has been sent.
    private void writeNotificationEntry(HealthDataType type, String value, String notificationText) {
        NotificationEntry entry = new NotificationEntry();
        entry.setType(type);
        entry.setValue(value);
        entry.setInstant(now);
        entry.setNotificationText(notificationText);
        storage.save(entry);
    }

}