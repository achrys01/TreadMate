package com.example.treadmill20app;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.example.treadmill20app.adapters.AppCtx;
import com.example.treadmill20app.adapters.devicesAdapter;
import com.example.treadmill20app.utils.MsgUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity that scans for treadmills and
 * initializes a Bluetooth connection, which
 * is established in device activity.
 * Based on BLE-GATT-Movesense-2.0 application provided by anderslm on github:
 * https://gits-15.sys.kth.se/anderslm/Ble-Gatt-Movesense-2.0
 **/

public class ScanTreadmillActivity extends MenuActivity {

    public static final int REQUEST_ENABLE_BT = 1000;
    public static final int REQUEST_ACCESS_LOCATION = 1001;
    public static final int EXTERNAL_STORAGE_PERMISSION_CODE = 23;

    public static String SELECTED_DEVICE = "Selected device";

    private static final long SCAN_PERIOD = 5000; // milliseconds

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private ArrayList<BluetoothDevice> mDeviceList;
    private devicesAdapter mBtDeviceAdapter;
    private TextView mScanInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.menu_frame);
        getLayoutInflater().inflate(R.layout.activity_scan_treadmill, contentFrameLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDeviceList = new ArrayList<>();
        mHandler = new Handler();

        // UI
        mScanInfoView = findViewById(R.id.infoView);
        mScanInfoView.setText(R.string.devices_info);
        Button startScanButton = findViewById(R.id.scanButton);

        startScanButton.setOnClickListener(v -> {
            mDeviceList.clear();
            scanForDevices(true);
        });

        RecyclerView recyclerView = findViewById(R.id.devicesView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mBtDeviceAdapter = new devicesAdapter(mDeviceList,
                this::onDeviceSelected);
        recyclerView.setAdapter(mBtDeviceAdapter);

        initBLE();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // stop scanning
        scanForDevices(false);
        mDeviceList.clear();
        mBtDeviceAdapter.notifyDataSetChanged();
    }

    // Check BLE permissions and turn on BT (if turned off) - user interaction(s)
    private void initBLE() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
        } else {
            // Access Location is a "dangerous" permission
            int hasAccessLocation = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasAccessLocation != PackageManager.PERMISSION_GRANTED) {
                // ask the user for permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ACCESS_LOCATION);
                // the callback method onRequestPermissionsResult gets the result of this request
            }
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // turn on BT
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    // Device selected, start DeviceActivity
    private void onDeviceSelected(int position) {
        BluetoothDevice selectedDevice = mDeviceList.get(position);
        Intent intent = new Intent(ScanTreadmillActivity.this, RunActivity.class);
        intent.putExtra(SELECTED_DEVICE, selectedDevice);
        startActivity(intent);
    }

    // Scan for BLE devices
    private void scanForDevices(final boolean enable) {
        final BluetoothLeScanner scanner =
                mBluetoothAdapter.getBluetoothLeScanner();
        if (enable) {
            if (!mScanning) {
                // stop scanning after a pre-defined scan period, SCAN_PERIOD
                mHandler.postDelayed(() -> {
                    if (mScanning) {
                        mScanning = false;
                        scanner.stopScan(mScanCallback);
                    }
                }, SCAN_PERIOD);

                mScanning = true;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    MsgUtils.showToast(AppCtx.getContext(), "Enable nearby devices permission for the app");
                    return;
                }
                scanner.startScan(mScanCallback);
                mScanInfoView.setText(R.string.devices_info);
            }
        } else {
            if (mScanning) {
                mScanning = false;
                scanner.stopScan(mScanCallback);
            }
        }

        int hasAccessStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasAccessStorage != PackageManager.PERMISSION_GRANTED) {
            // ask the user for permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSION_CODE);
            // the callback method onRequestPermissionsResult gets the result of this request
        }

    }

    // Implementation of scan callback methods
    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //Log.i(LOG_TAG, "onScanResult");
            final BluetoothDevice device = result.getDevice();
            final String name = device.getName();

            mHandler.post(() -> {
                if (name != null
                        && name.contains("rpi")
                        && !mDeviceList.contains(device)) {
                    mDeviceList.add(device);
                    mBtDeviceAdapter.notifyDataSetChanged();
                    String info = "Found " + mDeviceList.size() + " device(s)\n"
                            + "Touch to connect";
                    mScanInfoView.setText(info);
                }
            });
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


    // callback for Activity.requestPermissions
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ACCESS_LOCATION) {
            // if request is cancelled, the results array is empty
            if (grantResults.length == 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // stop this activity
                this.finish();
            }
        }else if (requestCode == EXTERNAL_STORAGE_PERMISSION_CODE) {
            // if request is cancelled, the results array is empty
            if (grantResults.length == 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // stop this activity
                this.finish();
            }
        }
    }

    // callback for request to turn on BT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if user chooses not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.menu_start);
    }
}