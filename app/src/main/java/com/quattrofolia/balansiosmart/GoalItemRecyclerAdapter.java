package com.quattrofolia.balansiosmart;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quattrofolia.balansiosmart.models.Goal;

import java.util.List;

public class GoalItemRecyclerAdapter extends RecyclerView.Adapter<GoalItemRecyclerAdapter.GoalViewHolder> {

    private static final String TAG = "GoalItemRecyclerAdapter";

    private List<Goal> goals;

    public static class GoalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Declare required views for goal items
        private TextView countView;
        private TextView typeView;

        public GoalViewHolder(View v) {
            super(v);
            countView = (TextView) v.findViewById(R.id.goalItemCount);
            typeView = (TextView) v.findViewById(R.id.goalItemType);
            v.setOnClickListener(this);
        }

        public void bindGoal(List<Goal> goals, int position) {
            String count = String.valueOf(position + 1);
            countView.setText(count);
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
