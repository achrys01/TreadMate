package com.example.treadmill20app.utils;
/*
This class Calculates the heart rate based on data from heart rate measurement characteristic
From: https://gits-15.sys.kth.se/anderslm/Ble-Gatt-with-Service
 */
import android.bluetooth.BluetoothGattCharacteristic;

import static com.example.treadmill20app.utils.HeartRateServiceUUIDs.HEART_RATE_MEASUREMENT;

public class HeartRateUtils {

    // TODO: handle out of bounds values, including "start value" 72.
    public static int calculateHeartRateValue(final BluetoothGattCharacteristic characteristic) {
        if (!HEART_RATE_MEASUREMENT.equals(characteristic.getUuid()))
            throw new IllegalArgumentException("not a heart rate characteristic");

        // This is special handling for the Heart Rate Measurement profile.
        // Data parsing is carried out as per profile specifications:
        // https://www.bluetooth.com/specifications/gatt/characteristics/
        int flag = characteristic.getProperties();
        int format = -1;
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;

        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
        }
        int heartRate = characteristic.getIntValue(format, 1);
        return heartRate;
    }
}
