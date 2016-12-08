package com.quattrofolia.balansiosmart;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quattrofolia.balansiosmart.models.Discipline;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;

import org.joda.time.Instant;
import org.joda.time.Interval;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class GoalItemRecyclerAdapter extends RecyclerView.Adapter<GoalItemRecyclerAdapter.GoalViewHolder> {

    private static final String TAG = "GoalItemRecyclerAdapter";

    private List<Goal> goals;

    public static class GoalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Declare required views for goal items
        private CompletionRing completionRing;
        private TextView typeView;

        public GoalViewHolder(View v) {
            super(v);
            completionRing = (CompletionRing) v.findViewById(R.id.goalItemCompletionCircle);
            typeView = (TextView) v.findViewById(R.id.goalItemType);
            v.setOnClickListener(this);
        }

        public void bindGoal(List<Goal> goals, int position) {
            String count = String.valueOf(position + 1);
            Goal goal = goals.get(position);
            Discipline discipline = goal.getDiscipline();

            if (discipline != null) {
                Instant now = new Instant();
                float frequency = discipline.getFrequency();
                Interval currentPeriod = discipline.getMonitoringPeriod().quantizedInterval(now, 0);
                RealmResults<HealthDataEntry> entries;
                entries = Realm.getDefaultInstance()
                        .where(HealthDataEntry.class)
                        .greaterThan("instant", currentPeriod.getStartMillis())
                        .lessThan("instant", currentPeriod.getEndMillis())
                        .findAll();
                float completion;
                if (frequency > 0) {
                    completion = (float) entries.size() / frequency;
                } else {
                    completion = 0;
                }

                /* Update completion view */
                completionRing.setCompletion(completion);
            } else {
                completionRing.disable();
            }
            typeView.setText(goals.get(position).getType().getLongName());
        }

        // Handle required events
        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick");
        }
    }

    public GoalItemRecyclerAdapter(List<Goal> goals) {
        this.goals = goals;
    }

    public void setItemList(List<Goal> goals) {
        this.goals = goals;
        this.notifyDataSetChanged();
    }

    @Override
    public GoalItemRecyclerAdapter.GoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.progress_view_goal_item_row, parent, false);
        return new GoalViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(GoalViewHolder holder, int position) {
        holder.bindGoal(goals, position);
    }

    @Override
    public int getItemCount() {
        if (goals != null) {
            return goals.size();
        } else {
            return 0;
        }
    }
}
