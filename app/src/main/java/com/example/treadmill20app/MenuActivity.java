package com.example.treadmill20app;
/*
Set the menu activity for all activities
Based on: https://github.com/jitseve/XFran
*/

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.treadmill20app.BtServices.BleHeartRateService;
import com.example.treadmill20app.BtServices.GattActions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import static com.example.treadmill20app.BtServices.GattActions.ACTION_GATT_HEART_RATE_EVENTS;
import static com.example.treadmill20app.BtServices.GattActions.HR_EVENT;
import static com.example.treadmill20app.BtServices.GattActions.HEART_RATE_DATA;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = MenuActivity.class.getSimpleName();

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu accountMenu;
    FirebaseAuth firebaseAuth;
    boolean treadmillConnected, hrConnected;
    private String mHRdeviceAddress, mHRdeviceName;
    private BleHeartRateService mBluetoothLeService;
    private Handler mHandler;

    public static int heartRateMeas;

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
        accountMenu.findItem(R.id.menu_workout).setVisible(false);

        navigationView.bringToFront();
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        /*------ USER AUTH ------*/
        checkFirebaseConnection();
        checkHRConnection();
        mHandler = new Handler();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        mHRdeviceAddress = ScanHRActivity.getHRdeviceAddress();
        Log.i(TAG, "device address" + mHRdeviceAddress);
        mHRdeviceName = ScanHRActivity.getHRdeviceName();
        Log.i(TAG, "device name" + mHRdeviceName);
        if (mHRdeviceAddress != null) {
            Intent gattServiceIntent = new Intent(this, BleHeartRateService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        }
        checkHRConnection();
        checkFirebaseConnection();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkHRConnection(){
        if (hrConnected) {
            //accountMenu.findItem(R.id.menu_hr_connect).setTitle(R.string.menu_hr_connected);
            accountMenu.findItem(R.id.menu_hr_connect).setTitle("HR sensor: " + mHRdeviceName);
            accountMenu.findItem(R.id.menu_hr_connect).setIconTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_green)));
        }
        if (treadmillConnected) {
            accountMenu.findItem(R.id.menu_treadmill_connect).setTitle(R.string.menu_treadmill_connected);
            accountMenu.findItem(R.id.menu_treadmill_connect).setIconTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_green)));
        }
    }

    private void checkFirebaseConnection(){
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            accountMenu.findItem(R.id.menu_login).setVisible(false);
            accountMenu.findItem(R.id.menu_profile).setVisible(true);
            accountMenu.findItem(R.id.menu_logout).setVisible(true);
            accountMenu.findItem(R.id.menu_workout).setVisible(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        checkFirebaseConnection();
        checkHRConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public static int getHeartRateMeas(){
        return heartRateMeas;
    }

    //Callback methods to manage the Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BleHeartRateService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.i(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mHRdeviceAddress);
            Log.i(TAG, "onServiceConnected");

            hrConnected = true;
            mHandler.post(() -> {
                checkHRConnection();
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            Log.i(TAG, "onServiceDisconnected");
            hrConnected = false;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_HEART_RATE_EVENTS.equals(action)) {
                GattActions.HR_Event event = (GattActions.HR_Event) intent.getSerializableExtra(HR_EVENT);
                if (event != null) {
                    switch (event) {
                        case GATT_CONNECTED:
                        case HR_DATA_AVAILABLE:
                            //int heartRate = intent.getIntExtra(HEART_RATE_DATA, -1);
                            heartRateMeas = intent.getIntExtra(HEART_RATE_DATA, -1);
                            Log.i(TAG, "got data: " + heartRateMeas);
                        case GATT_SERVICES_DISCOVERED:
                        case GATT_DISCONNECTED:
                        default:
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
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
            case R.id.menu_workout:
                startActivity(new Intent(getApplicationContext(), WorkoutActivity.class));
                drawerLayout.closeDrawers();
                break;
            case R.id.menu_hr_connect:
                if (hrConnected) {
                    Log.i(TAG, "hrConnected");
                    startActivity(new Intent(getApplicationContext(), ConnectedActivity.class));
                }else{
                    startActivity(new Intent(getApplicationContext(), ScanHRActivity.class));
                }
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

