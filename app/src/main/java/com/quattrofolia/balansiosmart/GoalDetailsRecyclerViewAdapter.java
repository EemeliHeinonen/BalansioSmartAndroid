package com.quattrofolia.balansiosmart;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import com.quattrofolia.balansiosmart.storage.Storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by mrbeva on 11/30/16.
 */

public class GoalDetailsRecyclerViewAdapter extends RecyclerView.Adapter<GoalDetailsRecyclerViewAdapter.View_Holder>{

    RealmResults<HealthDataEntry> list;

    private SparseBooleanArray selectedItems;

    public GoalDetailsRecyclerViewAdapter(RealmResults<HealthDataEntry> list) {
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

    static class View_Holder extends RecyclerView.ViewHolder{

        TextView time;
        TextView measures;

        private View_Holder(View itemView) {
            super(itemView);

            time = (TextView) itemView.findViewById(R.id.time_of_measurement);
            measures = (TextView) itemView.findViewById(R.id.measurement);
        }
    }

    // method for select specific goals to delete.
    private void toggleSelection(int pos){
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    private void clearSelections(){
        selectedItems.clear();
        notifyDataSetChanged();
    }

    private int getSelectedItemCount(){
        return selectedItems.size();
    }

    private List<Integer> getSelectedItems(){
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    //function to be optimized
    public void deleteData(final HealthDataEntry healthData){
        Storage storage = new Storage();
        storage.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                healthData.deleteFromRealm();
            }
        });
    }

    public void deleteAll() {
        Storage storage = new Storage();
        storage.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                list.deleteAllFromRealm();
            }
        });
    }

    //function to be finished
    public void deleteGoal(){

    }
}
