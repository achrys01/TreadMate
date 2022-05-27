package com.example.treadmill20app;
/*
Login activity
From: https://firebase.google.com/docs/auth/android/firebaseui
*/

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.treadmill20app.utils.MsgUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import androidx.activity.result.ActivityResultLauncher;

public class LoginActivity extends MenuActivity {
// TODO Fix Google account login
    // Choose authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.menu_frame);
        getLayoutInflater().inflate(R.layout.activity_login, contentFrameLayout);

        if (firebaseAuth.getCurrentUser() != null) {
            MsgUtils.showToast(LoginActivity.this, "Already logged in");
            finish();
        } else {
            // Create and launch sign-in intent
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(R.drawable.ic_treadmill)
                    .setTheme(R.style.firebaseLoginTheme)
                    .build();
            signInLauncher.launch(signInIntent);
        }

    }

    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> onSignInResult(result)
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            MsgUtils.showToast(LoginActivity.this, "Successfully logged in");
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                MsgUtils.showToast(LoginActivity.this, "Login cancelled");
                return;
            }
            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                MsgUtils.showToast(LoginActivity.this, "Login failed, no internet connection");
                return;
            }
            MsgUtils.showToast(LoginActivity.this, "Login failed");
            Log.e("Login error", "error: ", response.getError());
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.menu_login);
    }

}
