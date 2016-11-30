package com.quattrofolia.balansiosmart;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import java.util.Collections;
import java.util.List;

/**
 * Created by mrbeva on 11/30/16.
 */

public class GoalDetailsRecyclerViewAdapter extends RecyclerView.Adapter<GoalDetailsRecyclerViewAdapter.View_Holder>{

    List<HealthDataEntry> list = Collections.emptyList();

    public GoalDetailsRecyclerViewAdapter(List<HealthDataEntry> list) {
        this.list=list;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_goal_details_list_item, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(View_Holder holder, int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        HealthDataEntry entry = list.get(position);
        holder.time.setText(entry.getInstant().toString());
        holder.measures.setText(entry.getValue());

    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

//    // Remove a RecyclerView item containing a specified Data object
//    public void remove(Data data) {
//        int position = list.indexOf(data);
//        list.remove(position);
//        notifyItemRemoved(position);
//    }

    static class View_Holder extends RecyclerView.ViewHolder{

        TextView time;
        TextView measures;

        private View_Holder(View itemView) {
            super(itemView);

            time = (TextView) itemView.findViewById(R.id.time_of_measurement);
            measures = (TextView) itemView.findViewById(R.id.measurement);
        }
    }
}
