package com.example.treadmill20app;

import android.app.Dialog;
import android.content.Context;
import androidx.appcompat.app.AlertDialog;

/*
This class is based on a similar class from the BLE-GATT-Movesense-2.0
application provided by anderslm on github:
https://gits-15.sys.kth.se/anderslm/Ble-Gatt-Movesense-2.0
 */
public class MsgUtils {

    // alert message
    public static Dialog createDialog(String title, String msg, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(" Ok", (dialog, id) -> {});
        return builder.create();
    }
}
