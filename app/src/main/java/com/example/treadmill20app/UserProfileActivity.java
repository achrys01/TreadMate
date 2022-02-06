package com.example.treadmill20app;
/*
Login activity
From: https://firebase.google.com/docs/firestore/quickstart?authuser=0
UI adapted from: https://github.com/karunstha/android-profile-ui/blob/master/app/src/main/res/layout/activity_main.xml

*/

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class UserProfileActivity extends MenuActivity {
    /*_________ VIEW _________*/
    private TextView mName, mEmail, mMaxHR, mMaxSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.menu_frame);
        getLayoutInflater().inflate(R.layout.activity_userprofile, contentFrameLayout);
        navigationView.setCheckedItem(R.id.menu_profile);

        /*----- HOOKS -----*//*
        mName = findViewById(R.id.profile_username);
        mEmail = findViewById(R.id.profile_useremail);
        mMaxHR = findViewById(R.id.profile_weight);
        mMaxSpeed = findViewById(R.id.profile_height);
        ImageButton edit = findViewById(R.id.profile_Edit);

        *//*-----  VM  -----*//*
        UserProfileVM mUserProfileVM = ViewModelProviders.of(this).get(UserProfileVM.class);
        mUserProfileVM.init();
        mUserProfileVM.getUserProfile().observe(this, this::setViews);

        *//*--- OBSERVER ---*//*
        edit.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class)));*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.menu_profile);
    }

/*    private void setViews(UserProfile userProfile){
        if (!userProfile.checkEmpty()){
            mName.setText(userProfile.getFullName());
            mEmail.setText(userProfile.getEmail());
            mMaxHR.setText(Double.toString(userProfile.getWeight()));
            mMaxSpeed.setText(Integer.toString(userProfile.getHeight()));
        }
    }*/
}
