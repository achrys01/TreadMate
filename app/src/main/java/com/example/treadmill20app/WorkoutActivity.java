package com.example.treadmill20app;
/*
Create new workout. Shows the HR track fragment and the Standard track fragment
*/

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.treadmill20app.adapters.WorkoutTabsAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.viewpager2.widget.ViewPager2;

public class WorkoutActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use the result layout in the set frame of the base activity
        FrameLayout contentFrameLayout = findViewById(R.id.menu_frame);
        getLayoutInflater().inflate(R.layout.activity_workout, contentFrameLayout);

        // Get the hooks needed for the tab layout
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager2 = findViewById(R.id.result_viewpager); //The VP allows sliding between the tabs

        // Initialise the viewpager adapter
        viewPager2.setAdapter(new WorkoutTabsAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    //The tab layout mediator allows switching between the fragments and changing of tab text
                    if (position == 0) {
                        tab.setText(R.string.workout_tab_1);
                    } else {
                        tab.setText(R.string.workout_tab_2);
                    }
                }).attach();
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationView.setCheckedItem(R.id.menu_workout);

    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.menu_workout);
    }
}