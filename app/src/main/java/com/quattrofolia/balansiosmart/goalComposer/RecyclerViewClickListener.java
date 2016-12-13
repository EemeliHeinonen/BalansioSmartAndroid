package com.quattrofolia.balansiosmart.goalComposer;

import android.view.View;

/**
 * Created by eemeliheinonen on 03/11/2016.
 */

//Interface for passing an item selected from the recyclerView in GoalTypeAdapter to GoalTypeFragment class

interface RecyclerViewClickListener {

    void recyclerViewListClicked(View v, int position, String itemName);
}
