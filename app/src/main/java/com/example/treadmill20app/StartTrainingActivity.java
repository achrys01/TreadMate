package com.example.treadmill20app;

import android.os.Bundle;
import android.widget.FrameLayout;

public class StartTrainingActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.menu_frame);
        getLayoutInflater().inflate(R.layout.activity_start_training, contentFrameLayout);

    }
}