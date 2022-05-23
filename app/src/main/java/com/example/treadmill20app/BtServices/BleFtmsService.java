package com.example.treadmill20app.BtServices;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import static com.example.treadmill20app.BtServices.GattActions.*;
import static com.example.treadmill20app.BtServices.GattActions.FTMS_Event;
import static com.example.treadmill20app.utils.FtmsServiceUUIDS.*;

import androidx.annotation.RequiresApi;


import com.example.treadmill20app.utils.TypeConverter;

import java.util.ArrayList;
import java.util.List;


public class BleFtmsService extends Service {

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice device = null;

    private BluetoothGattService mFtmsService = null;

    private BluetoothGattCharacteristic commandChar = null;

    private final Handler mHandler = new Handler();

    /*
    logging and debugging
     */
    private final static String TAG = BleFtmsService.class.getSimpleName();

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection
     * result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(...)} callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device - try to reconnect
        if (address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            return mBluetoothGatt.connect();
        }

        device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mBtGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        return true;
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources
     * are released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Callbacks to/from the treadmill service. The callbacks are executed on a worker thread.
     * Therefore, a Handler is used to update ui. Most common root of error is that an operation
     * has not finished when the net one is read. The new one will then not be executed. To prevent
     * this, a delay of 500 ms is put on the callbacks; onPostDelay.
     */
    private final BluetoothGattCallback mBtGattCallback = new BluetoothGattCallback() {

        final List<BluetoothGattCharacteristic> chars = new ArrayList<>();

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            System.out.println(status);
            System.out.println(newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server.");
                broadcastFtmsUpdate(FTMS_Event.GATT_CONNECTED);
                // attempt to discover services
                mBluetoothGatt.discoverServices();
            } //Not needed anymore, kept in just in case.
              //Update, apparently it is needed again.
            else if (status == 133 || status == 8) {
                //Unexplained error 133, is not described in documentation.
                //To get past it we only need to try again.
                //Error 8 is a timeout error. We manage to connect but, this error shows up
                //afterwards. To get past it we also just need to try again. Can take a while.
                try {
                    mBluetoothGatt.close();
                } catch (Exception e) {
                    //Do nothing
                }
                mHandler.postDelayed(() -> mBluetoothGatt = device.connectGatt(BleFtmsService.this, false, mBtGattCallback, BluetoothDevice.TRANSPORT_LE), 500);
                Log.i(TAG, "Connecting to GATT server.");
                broadcastFtmsUpdate(FTMS_Event.GATT_CONNECTING);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastFtmsUpdate(FTMS_Event.GATT_DISCONNECTED);
            }

        }

        //Service and its characteristics discovered. Subscribe to what interested.
        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                broadcastFtmsUpdate(FTMS_Event.GATT_SERVICES_DISCOVERED);
                // Debug: list discovered services
                logServices(gatt);

                // Get the ftms service
                mFtmsService = gatt.getService(FTMS_SERVICE);

                if (mFtmsService != null) {

                    broadcastFtmsUpdate(FTMS_Event.FTMS_SERVICE_DISCOVERED);
                    // debug: service present, list characteristics
                    logCharacteristics(mFtmsService);

                    //Find the control characteristic
                    commandChar = mFtmsService.getCharacteristic(TREADMILL_CONTROL_CHARACTERISTIC);
                    BluetoothGattCharacteristic dataCharacteristic =
                            mFtmsService.getCharacteristic(TREADMILL_DATA_CHARACTERISTIC);
                    //Find characteristics for status, supported speed and inclination
                    BluetoothGattCharacteristic speeds =
                            mFtmsService.getCharacteristic(SUPPORTED_SPEED_CHARACTERISTIC);
                    BluetoothGattCharacteristic inclinations =
                            mFtmsService.getCharacteristic(SUPPORTED_INCLINATION_CHARACTERISTIC);
                    BluetoothGattCharacteristic statusChar =
                            mFtmsService.getCharacteristic(FITNESS_MACHINE_STATUS_CHARACTERISTIC);
                    //Add characteristics to a list, to prevent timeouts. Used in requestCharacteristics method.
                    chars.add(speeds);
                    chars.add(inclinations);
                    chars.add(dataCharacteristic);
                    chars.add(statusChar);
                    chars.add(commandChar);

                    mHandler.postDelayed(() -> requestCharacteristics(gatt), 500);

                } else {
                    broadcastFtmsUpdate(FTMS_Event.FTMS_SERVICE_NOT_AVAILABLE);
                    Log.i(TAG, "FTMS service not available");
                }
            }
        }

        //Assign right methods to different characteristics.
        public void requestCharacteristics(BluetoothGatt gatt) {
            if ((chars.get(0).getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0)
                gatt.readCharacteristic(chars.get(0));
            else if ((chars.get(0).getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0)
                notifyCharacteristic(gatt, chars.get(0));
            else if ((chars.get(0).getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0)
                indicateCharacteristic(gatt, chars.get(0));

        }


        //Receive and parse notifiable and indicatable data from the treadmill.
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic) {
            Log.i(TAG, "onCharacteristicChanged " + characteristic.getUuid());

            if (TREADMILL_DATA_CHARACTERISTIC.equals(characteristic.getUuid())) {
                byte[] data = characteristic.getValue();

                double instSpeed = TypeConverter.BytesToUInt(data, 2, 2) / 100.0;
                int totDist = TypeConverter.BytesToUInt(data, 6, 3);
                double incl = TypeConverter.BytesToSInt(data, 9, 2) / 10.0;
                //System.out.println("Instantaneous speed: " + instSpeed);
                //Broadcast to runActivity
                broadcastTreadmillDataUpdate(instSpeed,totDist,incl);

            } else if (TREADMILL_CONTROL_CHARACTERISTIC.equals(characteristic.getUuid())) {
                byte[] data = characteristic.getValue();
                if (data[1] == 0 && data[2] == 1) {
                    System.out.println("Control permission granted");
                    mHandler.post(() -> {
                        byte[] start = {7};
                        commandChar.setValue(start);
                        boolean wasSuccess = mBluetoothGatt.writeCharacteristic(commandChar);
                        Log.d("writeCharacteristic", "Treadmill running state: " + wasSuccess);
                    });
                    //Broadcast to runActivity
                    broadcastControlUpdate("Control granted");
                } else if (data[2] == 1) {
                    if (data[1] == 2)
                        System.out.println("Speed control point successful");
                    else if (data[1] == 3)
                        System.out.println("Inclination control point successful");
                    else if (data[1] == 7) {
                        System.out.println("Treadmill successfully started");
                        //Broadcast to runActivity
                        broadcastControlUpdate("Start");
                    } else if (data[1] == 8) {
                        System.out.println("Treadmill successfully stopped");
                        //Broadcast to runActivity
                        broadcastControlUpdate("Stop");
                    }
                } else if (data[2] == 3)
                    System.out.println("Control point out of range");
                else
                    System.out.println("Control operation failed");

            } else if (FITNESS_MACHINE_STATUS_CHARACTERISTIC.equals(characteristic.getUuid())) {
                byte[] data = characteristic.getValue();
                int type = data[0];
                int value;
                if(data.length == 3)
                    value = TypeConverter.BytesToUInt(data, 1, 2);
                else
                    value = data[1];
                //Broadcast to runActivity
                broadcastStatusUpdate(type,value);
            }
        }

        //Performed on readable characteristics
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            Log.i(TAG, "onCharacteristicRead " + characteristic.getUuid().toString());
            if (SUPPORTED_SPEED_CHARACTERISTIC.equals(characteristic.getUuid())) {
                byte[] speeds = characteristic.getValue();

                int minSpeed = TypeConverter.BytesToUInt(speeds, 0, 2);
                int maxSpeed = TypeConverter.BytesToUInt(speeds, 2, 2);
                double speedIncrement = TypeConverter.BytesToUInt(speeds, 4, 2) / 100.0;
                System.out.println("Min speed: " + minSpeed / 100.0);
                System.out.println("Max speed: " + maxSpeed / 100.0);
                System.out.println("Min speed increment: " + speedIncrement);

                //Broadcast to runActivity
                broadcastSupportedSpeedDataUpdate(minSpeed, maxSpeed, speedIncrement);
            }
            if (SUPPORTED_INCLINATION_CHARACTERISTIC.equals(characteristic.getUuid())) {
                byte[] inclinations = characteristic.getValue();

                int minIncl = TypeConverter.BytesToSInt(inclinations, 0, 2);
                int maxIncl = TypeConverter.BytesToSInt(inclinations, 2, 2);
                double inclIncrement = TypeConverter.BytesToSInt(inclinations, 4, 2) / 10.0;
                System.out.println("Min incl: " + minIncl / 10.0);
                System.out.println("Max incl: " + maxIncl / 10.0);
                System.out.println("Min incl increment: " + inclIncrement);

                broadcastSupportedInclDataUpdate(minIncl,maxIncl,inclIncrement);
            }

            chars.remove(chars.get(0));

            if (chars.size() > 0) {
                mHandler.postDelayed(() -> requestCharacteristics(gatt), 500);
            }
        }

        //Performed on notifiable characteristics. Timeout handling
        public void notifyCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            boolean success = gatt.setCharacteristicNotification(characteristic, true);
            if (success) {
                Log.i(TAG, "setCharactNotification success");
                //If success, subscribe to the notifications of the characteristic.
                BluetoothGattDescriptor descriptor =
                        characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            } else {
                Log.i(TAG, "setCharacteristicNotification failed");
            }

            chars.remove(chars.get(0));

            if (chars.size() > 0) {
                mHandler.postDelayed(() -> requestCharacteristics(gatt), 500);
            }
        }

        //Performed on indicatable characteristics. Timeout handling
        public void indicateCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            boolean success = gatt.setCharacteristicNotification(characteristic, true);
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            if (success) {
                Log.i(TAG, "setCharactNotification success");
                //If success, subscribe to the notifications of the characteristic.
                BluetoothGattDescriptor descriptor =
                        characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            } else {
                Log.i(TAG, "setCharacteristicNotification failed");
            }

            chars.remove(chars.get(0));

            mHandler.postDelayed(() -> {
                if (TREADMILL_CONTROL_CHARACTERISTIC.equals(characteristic.getUuid())) {   //ask for control permission
                    byte[] permission = {0};
                    commandChar.setValue(permission);
                    boolean wasSuccess = mBluetoothGatt.writeCharacteristic(commandChar);
                    Log.d("control permission", "Success: " + wasSuccess);
                }
                if (chars.size() > 0) {
                    requestCharacteristics(gatt);
                }
            }, 500);
        }

    };


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /*
    Android Service specific code for binding and unbinding to this Android service
     */
    public class LocalBinder extends Binder {
        public BleFtmsService getService() {
            return BleFtmsService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    /*
   Broadcast methods for events and data
    */
    private void broadcastFtmsUpdate(final FTMS_Event event) {
        final Intent intent = new Intent(ACTION_GATT_FTMS_EVENTS);
        intent.putExtra(FTMS_EVENT, event);
        sendBroadcast(intent);
    }

    private void broadcastTreadmillDataUpdate(final double instSpeed,final int totDist, final double incl) {
        final Intent intent = new Intent(ACTION_GATT_FTMS_EVENTS);
        intent.putExtra(FTMS_EVENT, FTMS_Event.FTMS_DATA_AVAILABLE);
        //Add ftms data to intent
        intent.putExtra(TOTAL_DISTANCE_DATA, totDist);
        intent.putExtra(INSTANT_SPEED_DATA, instSpeed);
        intent.putExtra(INCLINATION_DATA, incl);
        sendBroadcast(intent);
    }

    private void broadcastSupportedSpeedDataUpdate(final int minSpeed,final int maxSpeed, final double minIncr) {
        final Intent intent = new Intent(ACTION_GATT_FTMS_EVENTS);
        intent.putExtra(FTMS_EVENT, FTMS_Event.SUPPORTED_SPEED);
        //Add supported speed data to intent
        intent.putExtra(MIN_SPEED, minSpeed);
        intent.putExtra(MAX_SPEED, maxSpeed);
        intent.putExtra(SPEED_INCREMENT, minIncr);
        sendBroadcast(intent);
    }

    private void broadcastSupportedInclDataUpdate(final int minIncl,final int maxIncl, final double minIncr) {
        final Intent intent = new Intent(ACTION_GATT_FTMS_EVENTS);
        intent.putExtra(FTMS_EVENT, FTMS_Event.SUPPORTED_INCLINATION);
        //Add supported inclination to intent
        intent.putExtra(MIN_INCL, minIncl);
        intent.putExtra(MAX_INCL, maxIncl);
        intent.putExtra(INCL_INCREMENT, minIncr);
        sendBroadcast(intent);
    }


    private void broadcastControlUpdate(String control_type) {
        final Intent intent = new Intent(ACTION_GATT_FTMS_EVENTS);
        intent.putExtra(FTMS_EVENT, FTMS_Event.FTMS_CONTROL);
        //Add control updates to intent
        intent.putExtra(CONTROL_TYPE, control_type);
        sendBroadcast(intent);
    }


    private void broadcastStatusUpdate(int status_type, int value) {
        final Intent intent = new Intent(ACTION_GATT_FTMS_EVENTS);
        intent.putExtra(FTMS_EVENT, FTMS_Event.FTMS_STATUS);
        //Add status update to intent
        intent.putExtra(STATUS_TYPE, status_type);
        intent.putExtra(STATUS_VALUE, value);

        sendBroadcast(intent);
    }

    private void logServices(BluetoothGatt gatt) {
        List<BluetoothGattService> services = gatt.getServices();
        for (BluetoothGattService service : services) {
            String uuid = service.getUuid().toString();
            Log.i(TAG, "service: " + uuid);
        }
    }

    private void logCharacteristics(BluetoothGattService gattService) {
        List<BluetoothGattCharacteristic> characteristics =
                gattService.getCharacteristics();
        for (BluetoothGattCharacteristic chara : characteristics) {
            String uuid = chara.getUuid().toString();
            Log.i(TAG, "characteristic: " + uuid);
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_FTMS_EVENTS.equals(action)) {
                GattActions.FTMS_Event ftms_event = (GattActions.FTMS_Event) intent.getSerializableExtra(FTMS_EVENT);
                if (ftms_event != null) {
                    switch (ftms_event) {
                        case FTMS_COMMAND:
                            byte[] a = intent.getByteArrayExtra(COMMAND_CHAR);
                            commandChar.setValue(a);
                            boolean wasSuccess = mBluetoothGatt.writeCharacteristic(commandChar);
                            Log.d("writeCharacteristic", "Treadmill write success: " + wasSuccess);
                    }
                }
            }
        }
    };

    // Intent filter for broadcast updates from BleHeartRateServices and BleFtmsServices
    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_HEART_RATE_EVENTS);
        intentFilter.addAction(ACTION_GATT_FTMS_EVENTS);
        return intentFilter;
    }


}
