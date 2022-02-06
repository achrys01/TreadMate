package com.example.treadmill20app;
/*
Login activity
From: https://firebase.google.com/docs/auth/android/firebaseui
*/

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_SHORT;
import static androidx.core.content.PackageManagerCompat.LOG_TAG;

public class LoginActivity extends MenuActivity {

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
            Toast.makeText(LoginActivity.this, "Already logged in", LENGTH_SHORT).show();
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
            Toast.makeText(LoginActivity.this, "Successfully logged in", LENGTH_SHORT).show();
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                Toast.makeText(LoginActivity.this, "Login cancelled", LENGTH_SHORT).show();
                return;
            }
            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(LoginActivity.this, "Login failed, no internet connection", LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(LoginActivity.this, "Login failed", LENGTH_SHORT).show();
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
