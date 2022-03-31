package com.example.treadmill20app.utils;
/*
This class sets toasts and alerts to communicate with the user
From: -
*/

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class MsgUtils {

    public static void showToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showAlert(String title, String msg, Context context) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg).setPositiveButton("Ok", null)
                .show();
    }

}