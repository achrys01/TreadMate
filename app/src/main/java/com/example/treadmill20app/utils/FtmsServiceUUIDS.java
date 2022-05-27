package com.example.treadmill20app.utils;

import java.util.UUID;

/**
 * UUIDs of interest for the Treadmill service.
 */
public class FtmsServiceUUIDS {

    //Fitness machine service and characteristics
    public static final UUID FTMS_SERVICE =
            UUID.fromString("00001826-0000-1000-8000-00805F9B34FB");
    public static final UUID TREADMILL_DATA_CHARACTERISTIC =
            UUID.fromString("00002ACD-0000-1000-8000-00805F9B34FB");
    public static final UUID TREADMILL_CONTROL_CHARACTERISTIC =
            UUID.fromString("00002AD9-0000-1000-8000-00805F9B34FB");
    public static final UUID SUPPORTED_SPEED_CHARACTERISTIC =
            UUID.fromString("00002AD4-0000-1000-8000-00805F9B34FB");
    public static final UUID SUPPORTED_INCLINATION_CHARACTERISTIC =
            UUID.fromString("00002AD5-0000-1000-8000-00805F9B34FB");
    public static final UUID FITNESS_MACHINE_STATUS_CHARACTERISTIC =
            UUID.fromString("00002ADA-0000-1000-8000-00805F9B34FB");
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

}
