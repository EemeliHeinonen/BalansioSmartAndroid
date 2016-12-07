package com.quattrofolia.balansiosmart;

import com.quattrofolia.balansiosmart.models.Discipline;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import com.quattrofolia.balansiosmart.models.Range;
import com.quattrofolia.balansiosmart.models.User;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import io.realm.RealmList;

import static com.quattrofolia.balansiosmart.models.HealthDataType.BLOOD_GLUCOSE;
import static com.quattrofolia.balansiosmart.models.HealthDataType.WEIGHT;
import static com.quattrofolia.balansiosmart.models.MonitoringPeriod.day;
import static com.quattrofolia.balansiosmart.models.MonitoringPeriod.month;

public class DataModelTest {

    User user = new User();
    Goal bgGoal;
    Goal weightGoal;
    Discipline bgDiscipline = new Discipline();
    Discipline weightDiscipline = new Discipline();
    List<HealthDataEntry> healthDataEntries = new ArrayList<HealthDataEntry>();
    Range bgRange;
    Range weightRange;
    Random r = new Random();
    int ms;

    @Before
    public void createMeasurements() throws Exception {
        ms = 1;

        // Declare test dates
        DateTime now;
        DateTime yesterday;
        List<DateTime> bgRandomTimesYesterday;
        List<DateTime> bgRandomTimesToday;
        List<DateTime> weightRandomTimesThisMonth;


        // Init test dates
        now = new DateTime();
        yesterday = now.minusDays(1);
        int random = r.nextInt(4);
        bgRandomTimesYesterday = randomTimesInDay(random, yesterday);
        random = r.nextInt(5);
        bgRandomTimesToday = randomTimesInDay(random, now);
        random = r.nextInt(3);
        weightRandomTimesThisMonth = randomTimesInMonth(random, now);

        // Create test entries
        healthDataEntries = new ArrayList<>();
        System.out.println(bgRandomTimesYesterday.size() + " randomized bg entries for yesterday:");
        for (DateTime dt : bgRandomTimesYesterday) {
            HealthDataEntry entry = new HealthDataEntry();
            entry.setType(BLOOD_GLUCOSE);
            entry.setValue(randomBgValue());
            entry.setInstant(dt.toInstant());
            healthDataEntries.add(entry);
            printEntry(entry);
        }
        System.out.println(bgRandomTimesToday.size() + " randomized bg entries for today:");
        for (DateTime dt : bgRandomTimesToday) {
            HealthDataEntry entry = new HealthDataEntry();
            entry.setType(BLOOD_GLUCOSE);
            entry.setValue(randomBgValue());
            entry.setInstant(dt.toInstant());
            healthDataEntries.add(entry);
            printEntry(entry);
        }
        System.out.println(weightRandomTimesThisMonth.size() + " randomized weight entries for this month:");
        for (DateTime dt : weightRandomTimesThisMonth) {
            HealthDataEntry entry = new HealthDataEntry();
            entry.setType(WEIGHT);
            entry.setValue(randomWeightValue());
            entry.setInstant(dt.toInstant());
            healthDataEntries.add(entry);
            printEntry(entry);
        }
    }

    @Test
    public void createGoalForUser() throws Exception {
        bgDiscipline.setFrequency(4);
        bgDiscipline.setMonitoringPeriod(day);
        bgRange = new Range();
        bgRange.setLow(new BigDecimal(3.8));
        bgRange.setHigh(new BigDecimal(4.2));
        bgGoal = new Goal();
        bgGoal.setType(BLOOD_GLUCOSE);
        bgGoal.setDiscipline(bgDiscipline);
        bgGoal.setTargetRange(bgRange);

        weightDiscipline.setFrequency(1);
        weightDiscipline.setMonitoringPeriod(month);
        weightRange = new Range();
        weightRange.setLow(new BigDecimal(78));
        weightRange.setHigh(new BigDecimal(82));
        weightGoal = new Goal();
        weightGoal.setType(WEIGHT);
        weightGoal.setDiscipline(weightDiscipline);
        weightGoal.setTargetRange(weightRange);

        RealmList<Goal> goals = new RealmList<>();
        goals.add(bgGoal);
        goals.add(weightGoal);
        user.setGoals(goals);

        /*Following iterator checks the current progress status
        * on each progress_view_goal_item_row with previously generated mock values. */
        for (Goal goal : user.getGoals()) {

            Discipline discipline = goal.getDiscipline();

            // Define the period to which narrow down the sample pool
            Interval monitoringPeriod = discipline.getMonitoringPeriod().quantizedInterval(null, 0);
            System.out.println();
            System.out.println("Monitoring period " + monitoringPeriod.getStart() + " -> " + monitoringPeriod.getEnd());

            // Clone the sample pool
            List<HealthDataEntry> filteredEntries = new ArrayList<>(healthDataEntries);

            // Filter out everything but samples of correct type within monitoring period
            Iterator<HealthDataEntry> evalIterator = filteredEntries.iterator();
            while (evalIterator.hasNext()) {
                HealthDataEntry entry = evalIterator.next();
                if (!monitoringPeriod.contains(entry.getInstant()) ||
                        entry.getType() != goal.getType()) {
                    evalIterator.remove();
                }
            }

            System.out.println("filtered " + goal.getType() + " entries:");
            printEntries(filteredEntries);


            // Print completion result
            System.out.println("Goal "
                    + goal.getType()
                    + " completion for current "
                    + discipline.getMonitoringPeriod().name()
                    + ": "
                    + filteredEntries.size()
                    + "/"
                    + discipline.getFrequency());
        }
    }

    // Returns a random time on provided day
    private DateTime randomTimeOfDay(DateTime dateTime) {
        Random r = new Random();
        return new DateTime(
                dateTime.getYear(),
                dateTime.getMonthOfYear(),
                dateTime.getDayOfMonth(),
                r.nextInt(23),
                r.nextInt(59),
                r.nextInt(59)
        );
    }

    // Returns a random time on provided month
    private DateTime randomTimeOfMonth(DateTime dateTime) {
        Random r = new Random();
        return new DateTime(
                dateTime.getYear(),
                dateTime.getMonthOfYear(),
                r.nextInt(dateTime.dayOfMonth().getMaximumValue()-1)+1,
                r.nextInt(23),
                r.nextInt(59),
                r.nextInt(59)
        );
    }

    // Returns provided date with clock turned back to 00:00
    private DateTime midnightByDate(DateTime dateTime) {
        return new DateTime(
                dateTime.getYear(),
                dateTime.getMonthOfYear(),
                dateTime.getDayOfMonth(),
                0,
                0);
    }

    // Returns a list containing provided amount of random times on provided date
    private List<DateTime> randomTimesInDay(int amount, DateTime byDate) {
        List<DateTime> randomTimes = new ArrayList<>();
        for (int i = 0; i < amount; amount--) {
            randomTimes.add(randomTimeOfDay(byDate));
        }
        return randomTimes;
    }

    // Returns a list containing provided amount of random times on the month of provided date
    private List<DateTime> randomTimesInMonth(int amount, DateTime byDate) {
        List<DateTime> randomTimes = new ArrayList<>();
        for (int i = 0; i < amount; amount--) {
            randomTimes.add(randomTimeOfMonth(byDate));
        }
        return randomTimes;
    }


    // Returns a random BigDecimal for mocking blood glucose values
    private BigDecimal randomBgValue() {
        Number n = (Number) (r.nextInt(500) + 200);
        return new BigDecimal(n.toString()).movePointLeft(2);
    }

    // Returns a random BigDecimal for mocking weight values
    private BigDecimal randomWeightValue() {
        Number n = (Number) (r.nextInt(50) + 750);
        return new BigDecimal(n.toString()).movePointLeft(1);
    }

    // Prints each entry in the provided list to console in format:
    // <DateTime>: <HealthDataType>, <Value>
    private void printEntries(List<HealthDataEntry> entries) {
        if (entries.isEmpty()) {
            System.out.println("Empty.");
            return;
        }
        for (HealthDataEntry e : entries) {
            printEntry(e);
        }
    }

    private void printEntry(HealthDataEntry e) {
        System.out.println(new Instant(e.getInstant()).toDateTime().toString() + ": " + e.getType().toString() + ", " + e.getValue().toString());
    }
}
