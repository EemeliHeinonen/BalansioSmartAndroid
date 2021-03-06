package com.quattrofolia.balansiosmart.goalDetails;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;

import io.realm.RealmResults;

public class GoalDetailsRecyclerViewAdapter extends RecyclerView.Adapter<GoalDetailsRecyclerViewAdapter.HealthDataViewHolder>{

    private RealmResults<HealthDataEntry> list;

    public GoalDetailsRecyclerViewAdapter(RealmResults<HealthDataEntry> list) {
        this.list=list;
    }

    @Override
    public HealthDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_goal_details_list_item, parent, false);
        return new HealthDataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HealthDataViewHolder holder, int position) {
        // Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        HealthDataEntry entry = list.get(position);
        holder.time.setText(entry.getInstant().toString());
        holder.measures.setText(entry.getValue());
    }

    @Override
    public int getItemCount() {
        // Returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class HealthDataViewHolder extends RecyclerView.ViewHolder{
        TextView time;
        TextView measures;

        private HealthDataViewHolder(View itemView) {
            super(itemView);

            time = (TextView) itemView.findViewById(R.id.time_of_measurement);
            measures = (TextView) itemView.findViewById(R.id.measurement);
        }
    }

}
