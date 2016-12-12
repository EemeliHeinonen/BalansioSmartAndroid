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
import com.quattrofolia.balansiosmart.models.User;
import com.quattrofolia.balansiosmart.storage.Storage;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.Minutes;

import java.math.BigDecimal;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;


/**
 * Created by Mortti on 29.11.2016.
 */

public class NotificationIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    private static final String ACTION_REMOVE_GOAL_NOTIFICATIONS = "ACTION_REMOVE_GOAL_NOTIFICATIONS";
    private static String TAG = "jeee";
    private String realmTestString;

    // Storage
    private Realm realm;
    private RealmChangeListener realmChangeListener;
    private Storage storage;

    private Instant now;

    private RealmResults<Goal> allGoals;
    private Goal weightGoal;
    private Goal bgGoal;
    private Goal bpsGoal;
    private Goal bpdGoal;
    private Goal sleepGoal;
    private Goal exerciseGoal;
    private Goal nutritionGoal;
    private Duration twoHours;

    private RealmResults<NotificationEntry> allNotificationEntries;
    private RealmResults<NotificationEntry> easyDisciplineNotificationEntries;
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
            Log.d(TAG, "onHandleIntent: Action: " + action);
            if (ACTION_START.equals(action)) {
                Log.d(TAG, "onHandleIntent: actionstart equals action");
                processStartNotification();
            }
            if (ACTION_DELETE.equals(action)) {
                Log.d(TAG, "onHandleIntent: action delete equals action");
                processDeleteNotification(intent);
            }
            if (ACTION_REMOVE_GOAL_NOTIFICATIONS.equals(action)) {
                Log.d(TAG, "onHandleIntent: ACTION_REMOVE_GOAL_NOTIFICATIONS");
                removeGoalNotifications(intent.getIntExtra("notificationId", 0), intent.getStringExtra("type"));

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

    private void removeGoalNotifications(int notificationId, String goalType) {
        // TODO: get goal and change notificationstyle, and store with storage
        Log.d(TAG, "removeGoalNotifications: for " + goalType);
        realm = Realm.getDefaultInstance();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (realm != null) {
            realm.beginTransaction();
            Goal targetGoal = realm.where(Goal.class).equalTo("type", goalType).findFirst();
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


    private void processStartNotification() {
        Log.d(TAG, "processStartNotification: ");
        // Do something. For example, fetch fresh data from backend to create a rich notification?
        now = new Instant();
        realm = Realm.getDefaultInstance();
        final Session session = BalansioSmart.currentSession(realm);
        final int id = session.getUserId().intValue();
        storage = new Storage();
        twoHours = Duration.standardHours(2);

        User managedUser = realm.where(User.class).equalTo("id", id).findFirst();


        initGoals();
        initEntries();
        initNotificationEntries();
        easyDisciplineCheck();
        easyClinicalCheck();

        Log.d(TAG, "processStartNotification: WeekOfWeekYear test: " + now.toDateTime().weekOfWeekyear().getAsText());
        Log.d(TAG, "processStartNotification: allGoals size: " + allGoals.size());
        Log.d(TAG, "processStartNotification: last goal's type" + allGoals.get(allGoals.size() - 1).getType().getLongName());
        //Log.d(TAG, "processStartNotification: weightGoal TEST: "+weightGoal.getType().getLongName());
        //Log.d(TAG, "processStartNotification: bgGoal TEST: "+bgGoal.getType().getLongName());

        ///////Entry check testing
        //Instant lastEntryTime = managedUser.entries.get(managedUser.entries.size() - 1).getInstant();
        //isThisWeek(lastEntryTime.toDateTime());
        //Log.d(TAG, "processStartNotification: isToday: " + isToday(lastEntryTime.toDateTime()));
        //Log.d(TAG, "processStartNotification: Last Entry time: " + lastEntryTime);
        //Log.d(TAG, "processStartNotification: difference in time(minutesBetween): " + Minutes.minutesBetween(lastEntryTime, now));


        /*if (managedUser.entries.size() > 5) {
            sendNotification("Scheduled Notification", "This notification has been triggered by Notification Service");
        } else {
            sendNotification("Scheduled Notification", "there's less than 5 entries");
        }*/
    }

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
                        .putExtra("type", goalType.getLongName())
                        .putExtra("notificationId", NOTIFICATION_ID),
                        PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent removeNotificationsIntent = PendingIntent.getService(this,
                NOTIFICATION_ID,
                new Intent(this, NotificationIntentService.class)
                        .putExtra("type", goalType.name())
                        .putExtra("notificationId", NOTIFICATION_ID)
                        .setAction(ACTION_REMOVE_GOAL_NOTIFICATIONS),
                        PendingIntent.FLAG_UPDATE_CURRENT);


        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(title)
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentText(text)
                .setSmallIcon(R.drawable.bg)
                .addAction(R.drawable.bg, "Remind later", progressViewIntent)
                .addAction(R.drawable.bg, "Edit", goalComposerIntent).setAutoCancel(true)
                .addAction(R.drawable.bg, "Do not notify about this", removeNotificationsIntent).setAutoCancel(true);


        builder.setContentIntent(progressViewIntent);
        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));


        manager.notify(NOTIFICATION_ID, builder.build());
    }


    private void initGoals() {
        allGoals = realm.where(Goal.class).findAll();
        weightGoal = realm.where(Goal.class).equalTo("type", "WEIGHT").findFirst();
        bgGoal = realm.where(Goal.class).equalTo("type", "BLOOD_GLUCOSE").findFirst();
        bpsGoal = realm.where(Goal.class).equalTo("type", "BLOOD_PRESSURE_SYSTOLIC").findFirst();
        bpdGoal = realm.where(Goal.class).equalTo("type", "BLOOD_PRESSURE_DIASTOLIC").findFirst();
        sleepGoal = realm.where(Goal.class).equalTo("type", "SLEEP").findFirst();
        exerciseGoal = realm.where(Goal.class).equalTo("type", "EXERCISE").findFirst();
        nutritionGoal = realm.where(Goal.class).equalTo("type", "NUTRITION").findFirst();
    }

    private void initEntries() {
        allEntries = realm.where(HealthDataEntry.class).findAll();
        bgEntries = realm.where(HealthDataEntry.class).equalTo("type", "BLOOD_GLUCOSE").findAll();
        weightEntries = realm.where(HealthDataEntry.class).equalTo("type", "WEIGHT").findAll();
        bpsEntries = realm.where(HealthDataEntry.class).equalTo("type", "BLOOD_PRESSURE_SYSTOLIC").findAll();
        bpdEntries = realm.where(HealthDataEntry.class).equalTo("type", "BLOOD_PRESSURE_DIASTOLIC").findAll();
        sleepEntries = realm.where(HealthDataEntry.class).equalTo("type", "SLEEP").findAll();
        exerciseEntries = realm.where(HealthDataEntry.class).equalTo("type", "EXERCISE").findAll();
        nutritionEntries = realm.where(HealthDataEntry.class).equalTo("type", "NUTRITION").findAll();
    }

    private void initNotificationEntries() {
        allNotificationEntries = realm.where(NotificationEntry.class).findAll();
        easyDisciplineNotificationEntries = realm.where(NotificationEntry.class).equalTo("value", "easyDiscipline").findAll();

    }

    private void easyDisciplineCheck() {
        // TODO: check that goals monitoringperiod is day
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
                            writeNotificationEntry(easyDisciplineGoals.get(i).getType(), "easyDiscipline");
                            sendNotification(easyDisciplineGoals.get(i).getType().getLongName() + "goal has failed", "You didn't accomplish your goal this time", easyDisciplineGoals.get(i).getType());
                        } else if (currentNotificationEntries
                                .last().getInstant().isBefore(now.minus(twoHours))) {

                            Log.d(TAG, "easyDisciplineCheck: goal failed");
                            writeNotificationEntry(easyDisciplineGoals.get(i).getType(), "easyDiscipline");
                            sendNotification(easyDisciplineGoals.get(i).getType().getLongName() + "goal has failed", "You didn't accomplish your goal this time", easyDisciplineGoals.get(i).getType());
                        }
                    } else {
                        Log.d(TAG, "easyDisciplineCheck: goal accomplished / has already been notified about");
                    }
                }
            }

            //Log.d(TAG, "easyDisciplineCheck: number of entrys of the first item in the easyDisciplineGoals list: "+ i);
            Log.d(TAG, "easyDisciplineCheck: easyDisciplineGoals Size: " + easyDisciplineGoals.size());
            Log.d(TAG, "easyDisciplineCheck: vikan easy itemin type: " + easyDisciplineGoals.get(easyDisciplineGoals.size() - 1).getType().getLongName());
            Log.d(TAG, "easyDisciplineCheck: vikan easy itemin discipline: " + easyDisciplineGoals.get(easyDisciplineGoals.size() - 1).getDiscipline().toString());
        } else {
            Log.d(TAG, "easyDisciplineCheck: easyDisciplineGoals is empty");
        }
    }

    private void easyClinicalCheck() {

        if (isWakingHours()) {
            RealmResults<Goal> easyClinicalGoals = allGoals.where().equalTo("notificationStyle", "Easy").isNotNull("targetRange").findAll();
            if (easyClinicalGoals.size() > 0) {
                Log.d(TAG, "easyClinicalCheck: easyClinical Typen Test log: " + easyClinicalGoals.first().getType().toString());
                for (Goal goal : easyClinicalGoals) {

                    int numberOfFailedEntries = 0;
                    RealmResults<HealthDataEntry> currentEasyClinicalEntries = allEntries.where().equalTo("type", goal
                            .getType().toString()).findAll();
                    if (currentEasyClinicalEntries.size() > 4) {
                        RealmResults<NotificationEntry> currentNotificationEntries = allNotificationEntries.where().equalTo("type", goal.getType().toString()).equalTo("value", "easyClinical").findAll();
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
                    Log.d(TAG, "easyClinicalCheck: number of failed Entries: " + numberOfFailedEntries);
                    if (numberOfFailedEntries >= 5) {
                        writeNotificationEntry(goal.getType(), "easyClinical");
                        sendNotification(goal.getType().getLongName() + " clinical goal has failed", "You didn't accomplish your goal this time", goal.getType());
                    } else {
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
            Log.d(TAG, "isThisWeek: YES");
            return true;
        } else {
            Log.d(TAG, "isThisWeek: NO");
            return false;
        }
    }

    private boolean isThisMonth(DateTime dt) {
        DateTime.Property thisMonth = now.toDateTime().monthOfYear();
        DateTime.Property thisYear = now.toDateTime().year();
        if (dt.monthOfYear().getAsText().equals(thisMonth.getAsText()) && dt.year().getAsText().equals(thisYear.getAsText())) {
            Log.d(TAG, "isThisMonth: YES");
            return true;
        } else {
            Log.d(TAG, "isThisMonth: NO");
            return false;
        }
    }


    private boolean isLastHourOfMonitoringPeriod(DateTime dt) {
        Interval window = new Interval(dt.minusHours(12), dt.minusHours(2));
        if (window.contains(now)) {
            Log.d(TAG, "isLastHourOfMonitoringPeriod: YES");
            return true;
        } else {
            Log.d(TAG, "isLastHourOfMonitoringPeriod: NO");
            return false;
        }

    }

    private boolean isWakingHours() {
        Interval wakingHours = new Interval(now.toDateTime().withHourOfDay(8).withMinuteOfHour(0), now.toDateTime().withHourOfDay(22).withMinuteOfHour(0));
        if (wakingHours.contains(now)) {
            Log.d(TAG, "isWakingHours: YES");
            return true;
        } else {
            Log.d(TAG, "isWakingHours: NO");
            return false;
        }
    }

    private void writeNotificationEntry(HealthDataType type, String value) {
        NotificationEntry entry = new NotificationEntry();
        entry.setType(type);
        entry.setValue(value);
        entry.setInstant(now);
        storage.save(entry);
    }

}