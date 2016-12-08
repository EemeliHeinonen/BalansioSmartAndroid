package com.quattrofolia.balansiosmart;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import com.quattrofolia.balansiosmart.models.HealthDataType;

import java.util.List;

public class GoalItemRecyclerAdapter extends RecyclerView.Adapter<GoalItemRecyclerAdapter.GoalViewHolder> {

    private static final String TAG = "GoalItemRecyclerAdapter";

    private List<Goal> goals;
    private RecyclerViewClickListener clickListener;

    public GoalItemRecyclerAdapter(List<Goal> goals, RecyclerViewClickListener clickListener) {
        this.goals = goals;
        this.clickListener = clickListener;
    }

    public void setItemList(List<Goal> goals) {
        this.goals = goals;
        this.notifyDataSetChanged();
    }

    @Override
    public GoalItemRecyclerAdapter.GoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.progress_view_goal_item_row, parent, false);
        return new GoalViewHolder(inflatedView, clickListener);
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


    static class GoalViewHolder extends RecyclerView.ViewHolder {

        // Declare required views for goal items
        private TextView countView;
        private TextView typeView;
        private HealthDataType type;

        GoalViewHolder(View v, final RecyclerViewClickListener listener) {
            super(v);
            countView = (TextView) v.findViewById(R.id.goalItemCount);
            typeView = (TextView) v.findViewById(R.id.goalItemType);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.recyclerViewListClicked(view, getLayoutPosition(), type.toString());
                }
            });
        }

        void bindGoal(List<Goal> goals, int position) {
            String count = String.valueOf(position + 1);
            countView.setText(count);
            type = goals.get(position).getType();
            typeView.setText(type.getLongName());
        }

    }
}
