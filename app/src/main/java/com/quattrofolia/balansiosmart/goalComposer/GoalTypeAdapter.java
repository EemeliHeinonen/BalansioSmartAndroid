package com.quattrofolia.balansiosmart.goalComposer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.models.HealthDataType;

import java.util.List;

/**
 * Created by mrbeva on 10/29/16.
 */

//Class for populating the recyclerView in GoalTypeFragment

public class GoalTypeAdapter extends RecyclerView.Adapter<GoalTypeAdapter.ListDataHolder>{

    private List<GoalTypeListItem> listData;
    private LayoutInflater inflater;
    private static RecyclerViewClickListener itemListener;

    public GoalTypeAdapter (List<GoalTypeListItem> listData, Context context, RecyclerViewClickListener itemListener){
        this.inflater = LayoutInflater.from(context);
        this.listData= listData;
        this.itemListener = itemListener;
    }

    @Override
    public ListDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.goal_type_fragment_list_item, parent, false);
        return new ListDataHolder(view);
    }

    //populate the list of GoalTypeListItems
    @Override
    public void onBindViewHolder(ListDataHolder holder, int position) {
        GoalTypeListItem item = listData.get(position);
        holder.header.setText(item.getDataType().getLongName());
        holder.type = item.getDataType();
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ListDataHolder extends RecyclerView.ViewHolder{
        private Button header;
        private HealthDataType type;

        public ListDataHolder(final View itemView) {
            super(itemView);
            header = (Button) itemView.findViewById(R.id.button);
            header.setOnClickListener(new View.OnClickListener() {

                //Pass the selected item from this adapter to the GoalTypeFragment class
                public void onClick(View v) {

                    //Call the recyclerViewClickListener interfaces method to know which list item was clicked.
                    itemListener.recyclerViewListClicked(itemView, getLayoutPosition(), type);
                }
            });
        }
    }
}
