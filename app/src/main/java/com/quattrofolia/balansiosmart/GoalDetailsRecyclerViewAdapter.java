package com.quattrofolia.balansiosmart;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.quattrofolia.balansiosmart.models.Goal;
import com.quattrofolia.balansiosmart.models.HealthDataEntry;
import com.quattrofolia.balansiosmart.storage.Storage;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by mrbeva on 11/30/16.
 */

public class GoalDetailsRecyclerViewAdapter extends RecyclerView.Adapter<GoalDetailsRecyclerViewAdapter.View_Holder>{

    private Storage storage;
    private Realm realm;
    private RealmResults<Goal> allGoals;
    private RealmResults<HealthDataEntry> list;
    private RealmChangeListener realmChangeListener;



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

    public void deleteAll() {
//        Storage storage = new Storage();
//        storage.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                healthDataEntries.deleteAllFromRealm();
//            }
//        });
    }

    //function to be finished
    public void deleteGoal(){

    }
}
