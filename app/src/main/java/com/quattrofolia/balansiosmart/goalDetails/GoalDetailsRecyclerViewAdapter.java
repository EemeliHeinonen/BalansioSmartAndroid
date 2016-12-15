package com.quattrofolia.balansiosmart.goalDetails;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;

import java.text.DateFormat;
import java.util.Date;

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

        DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(holder.time.getContext());
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(holder.time.getContext());
        Date dateObject = entry.getInstant().toDateTime().toGregorianCalendar().getTime();
        String date = dateFormat.format(dateObject);
        String time = timeFormat.format(dateObject);

        String unit = entry.getType().getUnit().toString();

        holder.date.setText(date);
        holder.time.setText(time);
        holder.measures.setText(entry.getValue());
        holder.unit.setText(unit);
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
        TextView date;
        TextView time;
        TextView measures;
        TextView unit;

        private HealthDataViewHolder(View itemView) {
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.date_of_measurement);
            time = (TextView) itemView.findViewById(R.id.time_of_measurement);
            measures = (TextView) itemView.findViewById(R.id.measurement);
            unit = (TextView) itemView.findViewById(R.id.unit);
        }
    }

}
