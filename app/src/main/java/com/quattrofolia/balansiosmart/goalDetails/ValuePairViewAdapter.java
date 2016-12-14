package com.quattrofolia.balansiosmart.goalDetails;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quattrofolia.balansiosmart.R;

import java.util.List;


public class ValuePairViewAdapter extends RecyclerView.Adapter<ValuePairViewAdapter.ValuePairViewHolder> {

    private List<Pair<String, String>> valuePairs;

    public ValuePairViewAdapter(List<Pair<String, String>> valuePairs) {
        this.valuePairs = valuePairs;
    }

    public void setValuePairs(List<Pair<String, String>> valuePairs) {
        this.valuePairs = valuePairs;
        notifyDataSetChanged();
    }

    @Override
    public ValuePairViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_goal_detail_row, parent, false);
        return new ValuePairViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ValuePairViewHolder holder, int position) {
        String key = valuePairs.get(position).getL();
        String value = valuePairs.get(position).getR();
        holder.key.setText(key);
        holder.value.setText(value);
    }

    @Override
    public int getItemCount() {
        return valuePairs.size();
    }

    static class ValuePairViewHolder extends RecyclerView.ViewHolder {
        TextView key;
        TextView value;

        private ValuePairViewHolder(View view) {
            super(view);
            key = (TextView) view.findViewById(R.id.textView_goalDetailKey);
            value = (TextView) view.findViewById(R.id.textView_goalDetailValue);
        }
    }
}
