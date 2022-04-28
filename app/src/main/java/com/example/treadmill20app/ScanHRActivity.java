package com.example.treadmill20app;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.treadmill20app.adapters.AppCtx;
import com.example.treadmill20app.adapters.BtDeviceAdapter;
import com.example.treadmill20app.utils.MsgUtils;
import com.example.treadmill20app.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.bluetooth.le.ScanSettings.CALLBACK_TYPE_ALL_MATCHES;
import static com.example.treadmill20app.utils.HeartRateServiceUUIDs.ECG_SERVICE;
import static com.example.treadmill20app.utils.HeartRateServiceUUIDs.HEART_RATE_SERVICE;

public class ScanHRActivity extends MenuActivity {

    private static final String TAG = ScanHRActivity.class.getSimpleName();

    public static final int REQUEST_ENABLE_BT = 1000;
    private static final long SCAN_PERIOD = 5000; // milliseconds
    //public static String SELECTED_DEVICE = "Selected device";
    public static String deviceAddress;

    private static final List<ScanFilter> HEART_RATE_SCAN_FILTER;
    private static final ScanSettings SCAN_SETTINGS;

    static {
        ScanFilter heartRateServiceFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(HEART_RATE_SERVICE))
                .build();
        ScanFilter ECGServiceFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(ECG_SERVICE))
                .build();
        HEART_RATE_SCAN_FILTER = new ArrayList<>();
        HEART_RATE_SCAN_FILTER.add(heartRateServiceFilter);
        HEART_RATE_SCAN_FILTER.add(ECGServiceFilter);
        SCAN_SETTINGS = new ScanSettings.Builder().setScanMode(CALLBACK_TYPE_ALL_MATCHES).build();
    }

    //handling bluetooth connection
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private ArrayList<BluetoothDevice> mDeviceList;
    private BtDeviceAdapter mBtDeviceAdapter;
    private TextView mScanInfoView;
    private Button startScanButton;

    //permissions
    private String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_ADMIN
    };
    private PermissionUtils permissionUtils;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_scan_hr);

        FrameLayout contentFrameLayout = findViewById(R.id.menu_frame);
        getLayoutInflater().inflate(R.layout.activity_scan_hr, contentFrameLayout);

        //BLUETOOTH
        mDeviceList = new ArrayList<>();
        mHandler = new Handler();

        //HOOKS
        mScanInfoView = findViewById(R.id.scan_info);
        startScanButton = findViewById(R.id.start_scan_button);
        startScanButton.setOnClickListener(v -> {
            mDeviceList.clear();
            scanForDevices(true);
        });

        //HOOKS - Recycler view
        RecyclerView recyclerView = findViewById(R.id.scan_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mBtDeviceAdapter = new BtDeviceAdapter(mDeviceList, position -> onDeviceSelected(position));
        recyclerView.setAdapter(mBtDeviceAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationView.setCheckedItem(R.id.menu_run);

        mScanInfoView.setText(R.string.scan_start);
        initBLE();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.menu_run);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // stop scanning
        scanForDevices(false);
        mDeviceList.clear();
        mBtDeviceAdapter.notifyDataSetChanged();
    }

    // Check BLE permissions and turn on BT (if turned off)
    private void initBLE() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            MsgUtils.showToast(getApplicationContext(), "Bluetooth not supported!");
            finish();
        } else {
            permissionUtils = new PermissionUtils(this, PERMISSIONS);
            if(permissionUtils.arePermissionsEnabled()){
                Log.d(TAG, "Permission granted 1");
            } else {
                permissionUtils.requestMultiplePermissions();
            }
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Turn on BT, i.e. start an activity for the user consent
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    //intent back to run activity
    private void onDeviceSelected(int position) {
        BluetoothDevice selectedDevice = mDeviceList.get(position);

        /*
        Intent intent = new Intent(ScanHRActivity.this, RunActivity.class);
        //intent.putExtra(SELECTED_DEVICE, selectedDevice);
        intent.putExtra(MenuActivity.EXTRAS_DEVICE_NAME, selectedDevice.getName());
        intent.putExtra(MenuActivity.EXTRAS_DEVICE_ADDRESS, selectedDevice.getAddress());
        stopScanning();
         */
        deviceAddress = selectedDevice.getAddress();
        Log.i(TAG, "device address" + deviceAddress);
        stopScanning();
        finish();
        //Intent intent = new Intent(ScanHRActivity.this, HomeActivity.class);
        //startActivity(intent);
    }

    public static String getHRdeviceAddress(){
        return deviceAddress;
    }

    private void stopScanning() {
        if (mScanning) {
            BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
            scanner.stopScan(mScanCallback);
            mScanning = false;
            MsgUtils.showToast(AppCtx.getContext(), "Scanning stopped");
        }
    }

    private void scanForDevices(final boolean enable) {
        final BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (enable) {
            if (!mScanning) {
                // stop scanning after a pre-defined scan period, SCAN_PERIOD
                mHandler.postDelayed(() -> {
                    if (mScanning) {
                        mScanning = false;
                        scanner.stopScan(mScanCallback);
                        MsgUtils.showToast(ScanHRActivity.this, "Scanning stopped");
                    }
                }, SCAN_PERIOD);

                mScanning = true;
                scanner.startScan(HEART_RATE_SCAN_FILTER, SCAN_SETTINGS, mScanCallback); //scan for devices with HRS
                //scanner.startScan(mScanCallback); //scan all BLE devices
                mScanInfoView.setText(R.string.scan_fail);
                MsgUtils.showToast(ScanHRActivity.this, "Scanning for HR devices...");
            }
        } else {
            if (mScanning) {
                mScanning = false;
                scanner.stopScan(mScanCallback);
                MsgUtils.showToast(ScanHRActivity.this, "Scanning stopped");
            }
        }
    }

    // callback for for the BluetoothLeScanner
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            final BluetoothDevice device = result.getDevice();
            final String name = device.getName();
            mHandler.post(() -> {
                if (name != null && !mDeviceList.contains(device)) {
                    mDeviceList.add(device);
                    mBtDeviceAdapter.notifyDataSetChanged();
                    String info = "Found " + mDeviceList.size() + " device(s)\n"
                            + "Click to connect";
                    mScanInfoView.setText(info);
                }
            });
        }
    };

    // callback for ActivityCompat.requestPermissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            Log.d(TAG, "Permission granted 2");
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

}