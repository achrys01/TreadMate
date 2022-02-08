package com.example.treadmill20app;
/*
This activity displays broadcast messages from BleHeartRateService
From: -
 */
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.treadmill20app.BtServices.BleHeartRateService;

import static com.example.treadmill20app.BtServices.GattActions.*;
import static com.example.treadmill20app.BtServices.GattActions.ACTION_GATT_HEART_RATE_EVENTS;
import static com.example.treadmill20app.BtServices.GattActions.EVENT;
import static com.example.treadmill20app.BtServices.GattActions.HEART_RATE_DATA;

public class HeartRateActivity extends MenuActivity {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mHeartRateView;
    private TextView mStatusView;

    private String mDeviceAddress;

    private BleHeartRateService mBluetoothLeService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.menu_frame);
        getLayoutInflater().inflate(R.layout.activity_heart_rate, contentFrameLayout);

        // the intent from BleHeartRateService, that started this activity
        final Intent intent = getIntent();
        String deviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // set up ui references
        TextView deviceView = findViewById(R.id.device_view);
        deviceView.setText(deviceName);
        mHeartRateView = findViewById(R.id.heart_rate_view);
        mStatusView = findViewById(R.id.status_view);

        // NB! bind to the BleHeartRateService
        // Use onResume or onStart to register a BroadcastReceiver.
        Intent gattServiceIntent = new Intent(this, BleHeartRateService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    /*
    NB! Unbind from service when this activity is destroyed (the service itself
    might then stop).
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    /*
    Callback methods to manage the (BleHeartRate)Service lifecycle.
    */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BleHeartRateService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.i(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
            Log.i(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            Log.i(TAG, "onServiceDisconnected");
        }
    };

    /*
    A BroadcastReceiver handling various events fired by the Service, see GattActions.Event.
    */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_HEART_RATE_EVENTS.equals(action)) {
                Event event = (Event) intent.getSerializableExtra(EVENT);
                if (event != null) {
                    switch (event) {
                        case GATT_CONNECTED:
                        case GATT_DISCONNECTED:
                        case GATT_SERVICES_DISCOVERED:
                        case HEART_RATE_SERVICE_DISCOVERED:
                            mStatusView.setText(event.toString());
                            mHeartRateView.setText("-");
                            break;
                        case DATA_AVAILABLE:
                            int heartRate = intent.getIntExtra(HEART_RATE_DATA, -1);
                            Log.i(TAG, "got data: " + heartRate);
                            if (heartRate < 0) {
                                mStatusView.setText(R.string.unrecognized);
                                mHeartRateView.setText("?");
                            } else {
                                mStatusView.setText(R.string.heart_rate);
                                mHeartRateView.setText(String.format("%d", heartRate));
                            }
                            break;
                        case HEART_RATE_SERVICE_NOT_AVAILABLE:
                            mStatusView.setText(event.toString());
                            break;
                        default:
                            mStatusView.setText(R.string.unreachable);
                            mHeartRateView.setText("?");
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

    // logs
    private final static String TAG = HeartRateActivity.class.getSimpleName();
}
