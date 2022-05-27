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
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.treadmill20app.BtServices.BleFtmsService;
import com.example.treadmill20app.BtServices.BleHeartRateService;
import com.example.treadmill20app.BtServices.GattActions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import static com.example.treadmill20app.BtServices.GattActions.ACTION_GATT_FTMS_EVENTS;
import static com.example.treadmill20app.BtServices.GattActions.ACTION_GATT_HEART_RATE_EVENTS;
import static com.example.treadmill20app.BtServices.GattActions.CONTROL_TYPE;
import static com.example.treadmill20app.BtServices.GattActions.FTMS_EVENT;
import static com.example.treadmill20app.BtServices.GattActions.HR_EVENT;
import static com.example.treadmill20app.BtServices.GattActions.HEART_RATE_DATA;
import static com.example.treadmill20app.BtServices.GattActions.INCLINATION_DATA;
import static com.example.treadmill20app.BtServices.GattActions.INCL_INCREMENT;
import static com.example.treadmill20app.BtServices.GattActions.INSTANT_SPEED_DATA;
import static com.example.treadmill20app.BtServices.GattActions.MAX_INCL;
import static com.example.treadmill20app.BtServices.GattActions.MAX_SPEED;
import static com.example.treadmill20app.BtServices.GattActions.MIN_INCL;
import static com.example.treadmill20app.BtServices.GattActions.MIN_SPEED;
import static com.example.treadmill20app.BtServices.GattActions.SPEED_INCREMENT;
import static com.example.treadmill20app.BtServices.GattActions.STATUS_TYPE;
import static com.example.treadmill20app.BtServices.GattActions.STATUS_VALUE;
import static com.example.treadmill20app.BtServices.GattActions.TOTAL_DISTANCE_DATA;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = MenuActivity.class.getSimpleName();

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu accountMenu;
    FirebaseAuth firebaseAuth;
    boolean ftmsConnected, hrConnected;
    private String mHRdeviceAddress, mHRdeviceName;
    private BleHeartRateService mBleHRService;
    private String mFtmsDeviceAddress, mFtmsDeviceName;
    private BleFtmsService mBleFtmsService;
    private Handler mHandler;

    public static int heartRateMeas;

    private boolean isRunning;
    private static int maxSpeed;
    private static int minSpeed;
    private static double speedIncrement;
    private static int maxIncl;
    private static int minIncl;
    private static double inclIncrement;

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
        checkConnection();
        mHandler = new Handler();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        //heart rate connection
        mHRdeviceAddress = ScanHRActivity.getHRdeviceAddress();
        Log.i(TAG, "HR device address " + mHRdeviceAddress);
        mHRdeviceName = ScanHRActivity.getHRdeviceName();
        Log.i(TAG, "HR device name " + mHRdeviceName);
        if (mHRdeviceAddress != null) {
            Intent gattServiceIntent = new Intent(this, BleHeartRateService.class);
            bindService(gattServiceIntent, mHRServiceConnection, BIND_AUTO_CREATE);
        }

        //ftms connection
        mFtmsDeviceAddress = ScanTreadmillActivity.getFtmsDeviceAddress();
        Log.i(TAG, "FTMS device address " + mFtmsDeviceAddress);
        mFtmsDeviceName = ScanTreadmillActivity.getFtmsDeviceName();
        Log.i(TAG, "FTMS device name " + mFtmsDeviceName);
        if (mFtmsDeviceAddress != null) {
            Intent gattServiceIntent = new Intent(this, BleFtmsService.class);
            bindService(gattServiceIntent, mFtmsServiceConnection, BIND_AUTO_CREATE);
        }

        checkConnection();
        checkFirebaseConnection();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkConnection(){
        if (hrConnected) {
            //accountMenu.findItem(R.id.menu_hr_connect).setTitle(R.string.menu_hr_connected);
            accountMenu.findItem(R.id.menu_hr_connect).setTitle("HR sensor: " + mHRdeviceName);
            accountMenu.findItem(R.id.menu_hr_connect).setIconTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_green)));
        }
        if (ftmsConnected) {
            accountMenu.findItem(R.id.menu_treadmill_connect).setTitle("Treadmill: " + mFtmsDeviceName);
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
        checkConnection();
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

    //Callback methods to manage the HR Service lifecycle.
    private final ServiceConnection mHRServiceConnection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBleHRService = ((BleHeartRateService.LocalBinder) service).getService();
            if (!mBleHRService.initialize()) {
                Log.i(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBleHRService.connect(mHRdeviceAddress);
            Log.i(TAG, "onServiceConnected");

            hrConnected = true;
            mHandler.post(() -> {
                checkConnection();
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleHRService = null;
            Log.i(TAG, "onServiceDisconnected");
            hrConnected = false;
        }
    };

    //Callback methods to manage the Ftms Service lifecycle.
    private final ServiceConnection mFtmsServiceConnection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBleFtmsService = ((BleFtmsService.LocalBinder) service).getService();
            if (!mBleFtmsService.initialize()) {
                Log.i(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBleFtmsService.connect(mFtmsDeviceAddress);
            Log.i(TAG, "Ftms onServiceConnected");

            ftmsConnected = true;
            mHandler.post(() -> {
                checkConnection();
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleFtmsService = null;
            Log.i(TAG, "onServiceDisconnected");
            ftmsConnected = false;
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
            if (ACTION_GATT_FTMS_EVENTS.equals(action)) {
                GattActions.FTMS_Event ftms_event = (GattActions.FTMS_Event) intent.getSerializableExtra(FTMS_EVENT);
                if (ftms_event != null) {
                    switch (ftms_event) {
                        case GATT_CONNECTED:
                            //mConnectionView.setText(R.string.connected);
                            break;
                        case GATT_DISCONNECTED:
                            //mConnectionView.setText(R.string.disconnected);
                            break;
                        case GATT_CONNECTING:
                            //mConnectionView.setText(R.string.connecting);
                            break;
                        case GATT_SERVICES_DISCOVERED:
                        case FTMS_DATA_AVAILABLE:
                            double instSpeed = intent.getDoubleExtra(INSTANT_SPEED_DATA, -1);
                            double incl = intent.getDoubleExtra(INCLINATION_DATA, -1);
                            int totDist = intent.getIntExtra(TOTAL_DISTANCE_DATA, -1);

                            if(instSpeed != -1 ) {
                                Log.i(TAG, "got ftms data: ");
                                Log.i(TAG, "Speed: " + instSpeed);
                                Log.i(TAG, "Inclination: " + incl);
                                Log.i(TAG, "Distance: " + totDist);

                                //mTdView.setText(String.valueOf(totDist));
                                //The following two views are not displayed, this will not be needed!
                                //mSpeedView.setText(String.valueOf(instSpeed));
                                //mInclView.setText(String.valueOf(incl));
                            }

                            break;
                        case SUPPORTED_SPEED:
                            maxSpeed = intent.getIntExtra(MAX_SPEED,0);
                            minSpeed = intent.getIntExtra(MIN_SPEED,0);
                            speedIncrement = intent.getDoubleExtra(SPEED_INCREMENT,0);

                            //mSpeedBar.setMax((int) (maxSpeed * speedIncrement));
                            break;
                        case SUPPORTED_INCLINATION:
                            maxIncl = intent.getIntExtra(MAX_INCL,0);
                            minIncl = intent.getIntExtra(MIN_INCL,0);
                            inclIncrement = intent.getDoubleExtra(INCL_INCREMENT,0);

                            int inclIntervals = (int) (maxIncl / (10 * inclIncrement));
                            //mInclBar.setMax(inclIntervals);
                            break;
                        case FTMS_STATUS:
                            int type = intent.getIntExtra(STATUS_TYPE,-1);
                            int value = intent.getIntExtra(STATUS_VALUE,0);
                            if (type == 4) {
                                System.out.println("Treadmill started by user");
                                //StartStopButton.setBackgroundColor(Color.RED);
                                //StartStopButton.setText(R.string.stop);
                                isRunning = true;
                            } else if (type == 2 && value == 1) {
                                System.out.println("Treadmill stopped by user");
                                //StartStopButton.setBackgroundColor(Color.GREEN);
                                //StartStopButton.setText(R.string.start);
                                isRunning = false;
                            } else if (type == 5) {
                                String speedText = String.format(Locale.ENGLISH, "%.1f km/h", value / 100.0);
                                System.out.println("Speed changed to " + speedText);
                                //mControlSpeedView.setText(speedText);
                                //mSpeedBar.setProgress((int) (value * speedIncrement));
                            } else if (type == 6) {
                                String inclText = String.format(Locale.ENGLISH, "%.1f", value / 10.0) + " %";
                                System.out.println("Inclination changed to " + inclText);
                                //mControlInclView.setText(inclText);
                                //mInclBar.setProgress((int) (value / (inclIncrement * 10)));
                            }
                            break;
                        case FTMS_CONTROL:
                            String control_type = intent.getStringExtra(CONTROL_TYPE);
                            switch (control_type) {
                                case "Control granted":
                                    //StartStopButton.setBackgroundColor(Color.RED);
                                    //enable(true);
                                    break;
                                case "Start":
                                    //StartStopButton.setBackgroundColor(Color.RED);
                                    //StartStopButton.setText(R.string.stop);
                                    isRunning = true;
                                    break;
                                case "Stop":
                                    //StartStopButton.setBackgroundColor(Color.GREEN);
                                    //StartStopButton.setText(R.string.start);
                                    isRunning = false;
                                    break;
                            }


                    }
                }
            }
        }
    };

    // Intent filter for broadcast updates from BleHeartRateServices
    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_HEART_RATE_EVENTS);
        intentFilter.addAction(ACTION_GATT_FTMS_EVENTS);
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
                //startActivity(new Intent(getApplicationContext(), RunActivity.class)); //todo: if not connected, tell the user to connect
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
                if (ftmsConnected) {
                    Log.i(TAG, "ftmsConnected");
                    startActivity(new Intent(getApplicationContext(), ConnectedActivity.class));
                }else{
                    startActivity(new Intent(getApplicationContext(), ScanTreadmillActivity.class));
                }
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

