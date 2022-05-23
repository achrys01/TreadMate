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

    //A flag for event info in intents (via intent.putExtra)
    public final static String EVENT =
            "com.example.treadmill20app.BtServices.EVENT";

    //A flag for heart rate data in intent (via intent.putExtra)
    public final static String HEART_RATE_DATA =
            "com.example.treadmill20app.BtServices.HEART_RATE_DATA";

    //Events corresponding to Gatt status/events
    public enum Event {
        GATT_CONNECTED("Connected"),
        GATT_DISCONNECTED("Disconnected"),
        GATT_SERVICES_DISCOVERED("Services discovered"),
        HEART_RATE_SERVICE_DISCOVERED("Heart rate service"),
        HEART_RATE_SERVICE_NOT_AVAILABLE("Heart rate service unavailable"),
        DATA_AVAILABLE("Data available");

        @Override
        public String toString() {
            return text;
        }

        private final String text;

        private Event(String text) {
            this.text = text;
        }

    }
}
