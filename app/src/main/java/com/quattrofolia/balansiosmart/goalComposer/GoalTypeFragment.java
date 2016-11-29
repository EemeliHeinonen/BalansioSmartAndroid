package com.quattrofolia.balansiosmart.goalComposer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.RecyclerViewClickListener;

import static android.content.ContentValues.TAG;


/**
 * Created by eemeliheinonen on 27/10/2016.
 */

// Fragment for selecting the progress_view_goal_item_row data type.

public class GoalTypeFragment extends Fragment implements RecyclerViewClickListener {
    private RecyclerView recyclerView;
    private GoalTypeAdapter goalTypeAdapter;

    public static GoalTypeFragment newInstance() {
        GoalTypeFragment fragment = new GoalTypeFragment();
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout myView =(LinearLayout) inflater.inflate(R.layout.goal_type_fragment, container, false);
        recyclerView = (RecyclerView) myView.findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        goalTypeAdapter = new GoalTypeAdapter(GoalTypeListData.getListData(), getActivity(), this);
        recyclerView.setAdapter(goalTypeAdapter);
        return myView;
    }

    //Move to the next fragment by clicking a button on the recyclerView
    @Override
    public void recyclerViewListClicked(View v, int position, String itemName){
        Log.d(TAG, "recyclerViewListClicked: "+ position+ " "+ itemName);

        if (itemName.equals("Sleep")) {
            // Create fragment and pass the selected values as arguments to the next fragment
            Log.d(TAG, "onClick: about to create a fragment, goalType: " + itemName + " selected amount: " + 1 + " selected timeframe: " + "day");
            GoalRangeFragment newFragment = GoalRangeFragment.newInstance(itemName, 1, "day");
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit);

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        } else {

            // Create fragment and give the fragment change transaction an animation
            GoalIntensityFragment newFragment = GoalIntensityFragment.newInstance(itemName);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit);

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }
}
