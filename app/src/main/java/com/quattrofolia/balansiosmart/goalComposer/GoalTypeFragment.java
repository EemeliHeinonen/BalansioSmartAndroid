package com.quattrofolia.balansiosmart.goalComposer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.quattrofolia.balansiosmart.R;
import com.quattrofolia.balansiosmart.models.HealthDataType;
import com.quattrofolia.balansiosmart.models.MonitoringPeriod;

// Fragment for selecting the progress_view_goal_item_row data type.
public class GoalTypeFragment extends Fragment implements RecyclerViewClickListener {
    RecyclerView recyclerView;
    GoalTypeAdapter goalTypeAdapter;

    public static GoalTypeFragment newInstance() {
        GoalTypeFragment fragment = new GoalTypeFragment();
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.goal_type_fragment, container, false);
        recyclerView = (RecyclerView) myView.findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        goalTypeAdapter = new GoalTypeAdapter(GoalTypeListData.getListData(), getActivity(), this);
        recyclerView.setAdapter(goalTypeAdapter);
        return myView;
    }

    //Move to the next fragment and pass the selected data to it by clicking a button on the recyclerView
    @Override
    public void recyclerViewListClicked(View v, int position, HealthDataType dataType) {
        // Create fragment for the selected type, pass the selected values as arguments to the next fragment
        // and give the fragment change transaction an animation.

        Fragment fragment;
        FragmentTransaction transaction;

        switch (dataType) {
            case SLEEP:
                fragment = GoalRangeFragment.newInstance(dataType, 1, MonitoringPeriod.day);
                break;
            default:
                fragment = GoalIntensityFragment.newInstance(dataType);
        }
        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
