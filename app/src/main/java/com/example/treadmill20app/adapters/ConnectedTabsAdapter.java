package com.example.treadmill20app.adapters;

/*
Adapter to display the two fragments in a slider tab view
*/

import com.example.treadmill20app.ConnectedControlsFragment;
import com.example.treadmill20app.ConnectedStatsFragment;
import com.example.treadmill20app.WorkoutHRFragment;
import com.example.treadmill20app.WorkoutTrackFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ConnectedTabsAdapter extends FragmentStateAdapter {
    public ConnectedTabsAdapter(@NonNull FragmentActivity fragmentActivity){
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment

        if (position == 0) {
            // This displays the list of results in the left tab (pos =0)
            return new ConnectedControlsFragment();
        }
        // This displays the statisticsfragment in the right tab (pos != 0)
        return new ConnectedStatsFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
