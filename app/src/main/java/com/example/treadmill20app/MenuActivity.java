package com.example.treadmill20app;
/*
Set the menu activity for all activities
Based on: https://github.com/jitseve/XFran
*/

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.treadmill20app.BtServices.BleHeartRateService;
import com.example.treadmill20app.BtServices.GattActions;
import com.example.treadmill20app.utils.PermissionUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import static com.example.treadmill20app.BtServices.GattActions.ACTION_GATT_HEART_RATE_EVENTS;
import static com.example.treadmill20app.BtServices.GattActions.EVENT;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu accountMenu;
    FirebaseAuth firebaseAuth;
    boolean treadmillConnected, hrConnected;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        /*------ HOOKS ------*/
        navigationView = findViewById(R.id.menu_navigation_view);
        drawerLayout = findViewById(R.id.menu_drawer_layout);
        toolbar = findViewById(R.id.menu_toolbar);

        /*------ INIT ------*/
        setSupportActionBar(toolbar);

        accountMenu = navigationView.getMenu();
        accountMenu.findItem(R.id.menu_logout).setVisible(false);
        accountMenu.findItem(R.id.menu_profile).setVisible(false);

        navigationView.bringToFront();
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        /*------ USER AUTH ------*/
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            accountMenu.findItem(R.id.menu_login).setVisible(false);
            accountMenu.findItem(R.id.menu_profile).setVisible(true);
            accountMenu.findItem(R.id.menu_logout).setVisible(true);
        }

        /*------ SENSOR STATUS ------*/

        if (hrConnected){
            accountMenu.findItem(R.id.menu_hr_connect).setTitle(R.string.menu_hr_connected);
            accountMenu.findItem(R.id.menu_hr_connect).setIconTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_green)));
        }
        if (treadmillConnected){
            accountMenu.findItem(R.id.menu_treadmill_connect).setTitle(R.string.menu_treadmill_connected);
            accountMenu.findItem(R.id.menu_treadmill_connect).setIconTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_green)));
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_HEART_RATE_EVENTS.equals(action)) {
                GattActions.Event event = (GattActions.Event) intent.getSerializableExtra(EVENT);
                if (event != null) {
                    switch (event) {
                        case GATT_CONNECTED:
                        case DATA_AVAILABLE:
                        case GATT_SERVICES_DISCOVERED:
                            hrConnected = true;
                            break;
                        case GATT_DISCONNECTED:
                        default:
                            hrConnected = false;
                            break;
                    }
                }
            }
        }
    };

    // Intent filter for broadcast updates from BleHeartRateServices
    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_HEART_RATE_EVENTS);
        return intentFilter;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (firebaseAuth.getCurrentUser() != null) {
            accountMenu.findItem(R.id.menu_login).setVisible(false);
            accountMenu.findItem(R.id.menu_profile).setVisible(true);
            accountMenu.findItem(R.id.menu_logout).setVisible(true);
        }
        if (hrConnected){
            accountMenu.findItem(R.id.menu_hr_connect).setTitle(R.string.menu_hr_connected);
            accountMenu.findItem(R.id.menu_hr_connect).setIconTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_green)));
        }
        if (treadmillConnected){
            accountMenu.findItem(R.id.menu_treadmill_connect).setTitle(R.string.menu_treadmill_connected);
            accountMenu.findItem(R.id.menu_treadmill_connect).setIconTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_green)));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_home:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                drawerLayout.closeDrawers();
                break;
            case R.id.menu_run:
                startActivity(new Intent(getApplicationContext(), RunActivity.class)); //todo: if not connected, tell the user to connect
                drawerLayout.closeDrawers();
                break;
            case R.id.menu_login:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                drawerLayout.closeDrawers();
                break;
            case R.id.menu_profile:
                startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                drawerLayout.closeDrawers();
                break;
            case R.id.menu_choosewo:
                startActivity(new Intent(getApplicationContext(), WorkoutActivity.class));
                drawerLayout.closeDrawers();
                break;
            case R.id.menu_hr_connect:
                startActivity(new Intent(getApplicationContext(), ScanHRActivity.class)); //todo if connected g to activity that displays hr
                drawerLayout.closeDrawers();
                break;
            case R.id.menu_treadmill_connect:
                startActivity(new Intent(getApplicationContext(), ScanTreadmillActivity.class));
                drawerLayout.closeDrawers();
                break;
            case R.id.menu_logout:
                //firebaseAuth.signOut();
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                accountMenu.findItem(R.id.menu_logout).setVisible(false);
                                accountMenu.findItem(R.id.menu_profile).setVisible(false);
                                accountMenu.findItem(R.id.menu_login).setVisible(true);
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                drawerLayout.closeDrawers();
                                Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
                            }
                        });
        }
        return false;
    }
}

