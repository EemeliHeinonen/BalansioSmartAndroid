package com.quattrofolia.balansiosmart.goalList;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.goalDetails.GoalDetailsActivity;
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
    private Instant now;

    public static class GoalViewHolder extends RecyclerView.ViewHolder implements GoalItemClickListener {

        // Declare required views for goal items
        private View itemView;
        private CompletionRing completionRing;
        private LinearLayout accomplishmentsLayout;
        private TextView textViewAccomplishments;
        private TextView textViewFrequency;
        private TextView textViewPeriod;
        private TextView textViewType;
        private Context context;

        public GoalViewHolder(View v) {
            super(v);
            itemView = v;
            context = v.getContext();
            completionRing = (CompletionRing) v.findViewById(R.id.goalItem_completionRing);
            accomplishmentsLayout = (LinearLayout) v.findViewById(R.id.layout_accomplishments);
            textViewAccomplishments = (TextView) v.findViewById(R.id.textView_accomplishments);
            textViewFrequency = (TextView) v.findViewById(R.id.textView_disciplineFrequency);
            textViewPeriod = (TextView) v.findViewById(R.id.textView_period);
            textViewType = (TextView) v.findViewById(R.id.textView_goalItemType);
        }

        @Override
        public void onGoalItemClicked(Goal goal) {
            Intent intent = new Intent(itemView.getContext(), GoalDetailsActivity.class);
            intent.putExtra("GOAL_ID", goal.getId());
            context.startActivity(intent);
        }
    }

    public GoalItemRecyclerAdapter(List<Goal> goals) {
        this.goals = goals;
        this.now = new Instant();
    }

    public void setItemList(List<Goal> goals) {
        this.goals = goals;
        this.now = new Instant();
        this.notifyDataSetChanged();
    }

    @Override
    public GoalItemRecyclerAdapter.GoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.progress_view_goal_item_row, parent, false);
        return new GoalViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(final GoalViewHolder holder, final int position) {

        final Goal goal = goals.get(position);
        Discipline discipline = goal.getDiscipline();
        holder.textViewType.setText(goal.getType().getLongName());
        holder.textViewPeriod.setText("measurements");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.onGoalItemClicked(goal);
            }
        });

        if (discipline != null) {
            int frequency = discipline.getFrequency();
            Interval currentPeriod = discipline.getMonitoringPeriod().quantizedInterval(now, 0);
            RealmResults<HealthDataEntry> entries;
            entries = Realm.getDefaultInstance()
                    .where(HealthDataEntry.class)
                    .equalTo("type", goal.getType().toString())
                    .greaterThan("instant", currentPeriod.getStartMillis())
                    .lessThan("instant", currentPeriod.getEndMillis())
                    .findAll();
            float completion;
            if (frequency > 0) {
                completion = (float) entries.size() / (float) frequency;
            } else {
                completion = 0;
            }

            /* Update completion view */
            holder.completionRing.setCompletion(completion);
            holder.textViewFrequency.setText("" + frequency);
            holder.textViewAccomplishments.setText("" + entries.size());

        } else {
            holder.accomplishmentsLayout.setVisibility(View.INVISIBLE);
            holder.completionRing.disable();
        }

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
