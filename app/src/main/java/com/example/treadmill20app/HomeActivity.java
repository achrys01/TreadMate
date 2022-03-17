package com.example.treadmill20app;

import android.os.Bundle;
import android.widget.FrameLayout;

// Home Screen Activity
public class HomeActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.menu_frame);
        getLayoutInflater().inflate(R.layout.activity_home, contentFrameLayout);

    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.menu_home);
    }
}