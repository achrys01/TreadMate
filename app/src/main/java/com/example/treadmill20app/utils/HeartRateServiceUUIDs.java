package com.example.treadmill20app.utils;
/*
This class sets UUIDs for BLE HR service and characteristics
From BLE documentation: https://www.bluetooth.com/wp-content/uploads/Sitecore-Media-Library/Gatt/Xml/Characteristics/org.bluetooth.characteristic.heart_rate_measurement.xml
*/

import java.util.UUID;

public class HeartRateServiceUUIDs {

    //services
    //public static UUID HEART_RATE_SERVICE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    public static UUID HEART_RATE_SERVICE = convertFromInteger(0x180D);
    public static UUID ECG_SERVICE = convertFromInteger(0x1006);

    //characteristics
    //public static UUID HEART_RATE_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    public static UUID HEART_RATE_MEASUREMENT = convertFromInteger(0x2A37);
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID MANUFACTURER_NAME
            = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");

    public static UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }

}
