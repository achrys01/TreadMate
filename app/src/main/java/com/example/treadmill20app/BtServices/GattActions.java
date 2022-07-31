package com.example.treadmill20app.BtServices;
/*
This class displays broadcast messages from BleHeartRateService
From: https://gits-15.sys.kth.se/anderslm/Ble-Gatt-with-Service
 */

public class GattActions {

    //todo: see if this complies with https://www.iso.org/standard/61876.html

    //The action corresponding to heart rate events from BleHeartRateService.
    //Intended for IntentFilters for a BroadcastReceiver.
    public final static String ACTION_GATT_HEART_RATE_EVENTS =
            "com.example.treadmill20app.BtServices.ACTION_GATT_HEART_RATE_EVENTS";

    //The action corresponding to ftms events from BleFtmsService.
    public final static String ACTION_GATT_FTMS_EVENTS =
            "com.example.treadmill20app.BtServices.ACTION_GATT_Treadmill_EVENTS";

    //A flag for hr event info in intents (via intent.putExtra)
    public final static String HR_EVENT =
            "com.example.treadmill20app.BtServices.HR_EVENT";

    //A flag for ftms event info in intents (via intent.putExtra)
    public final static String FTMS_EVENT =
            "com.example.treadmill20app.BtServices.FTMS_EVENT";

    //A flag for heart rate data in intent (via intent.putExtra)
    public final static String HEART_RATE_DATA =
            "com.example.treadmill20app.BtServices.HEART_RATE_DATA";

    //Flags for treadmill data in intent (via intent.putExtra)
    public final static String TOTAL_DISTANCE_DATA =
            "com.example.treadmill20app.BtServices.TOTAL_DISTANCE_DATA";
    public final static String INSTANT_SPEED_DATA =
            "com.example.treadmill20app.BtServices.INSTANT_SPEED_DATA";
    public final static String INCLINATION_DATA =
            "com.example.treadmill20app.BtServices.INCLINATION_DATA";

    //Flags for supported speed data in intent (via intent.putExtra)
    public final static String MIN_SPEED =
            "com.example.treadmill20app.BtServices.MIN_SPEED";
    public final static String MAX_SPEED =
            "com.example.treadmill20app.BtServices.MAX_SPEED";
    public final static String SPEED_INCREMENT =
            "com.example.treadmill20app.BtServices.SPEED_INCREMENT";

    //Flags for supported inclination data in intent (via intent.putExtra)
    public final static String MIN_INCL =
            "com.example.treadmill20app.BtServices.INCL_SPEED";
    public final static String MAX_INCL =
            "com.example.treadmill20app.BtServices.INCL_SPEED";
    public final static String INCL_INCREMENT =
            "com.example.treadmill20app.BtServices.INCL_INCREMENT";

    //Flags for control characteristics in intent (via intent.putExtra)
    public final static String CONTROL_TYPE =
            "com.example.treadmill20app.BtServices.CONTROL_TYPE";
    public final static String CONTROL_VALUE =
            "com.example.treadmill20app.BtServices.CONTROL_VALUE";
    public final static String COMMAND_CHAR =
            "com.example.treadmill20app.BtServices.COMMAND_CHAR";
    //Flags for status characteristics in intent (via intent.putExtra)
    public final static String STATUS_TYPE =
            "com.example.treadmill20app.BtServices.STATUS_TYPE";
    public final static String STATUS_VALUE =
            "com.example.treadmill20app.BtServices.STATUS_VALUE";

    //Events corresponding to HR Gatt status/events
    public enum HR_Event {
        GATT_CONNECTED("Connected"),
        GATT_CONNECTING("Connecting"),
        GATT_DISCONNECTED("Disconnected"),
        GATT_SERVICES_DISCOVERED("Services discovered"),
        HEART_RATE_SERVICE_DISCOVERED("Heart rate service"),
        HEART_RATE_SERVICE_NOT_AVAILABLE("Heart rate service unavailable"),
        HR_DATA_AVAILABLE("HR Data available");

        @Override
        public String toString() {
            return text;
        }

        private final String text;

        private HR_Event(String text) {
            this.text = text;
        }

    }

    //Events corresponding to FTMS Gatt status/events
    public enum FTMS_Event {
        GATT_CONNECTED("Connected"),
        GATT_CONNECTING("Connecting"),
        GATT_DISCONNECTED("Disconnected"),
        GATT_SERVICES_DISCOVERED("Services discovered"),
        FTMS_SERVICE_DISCOVERED("FTMS service"),
        FTMS_SERVICE_NOT_AVAILABLE("FTMS service unavailable"),
        FTMS_DATA_AVAILABLE("Ftms Data available"),
        SUPPORTED_SPEED("Supported speed received"),
        SUPPORTED_INCLINATION("Supported inclination received"),
        FTMS_CONTROL("FTMS control changed"),
        FTMS_COMMAND("FTMS command sent"),
        FTMS_STATUS("FTMS status changed");

        @Override
        public String toString() {
            return text;
        }

        private final String text;

        private FTMS_Event(String text) {
            this.text = text;
        }

    }
}
