package com.example.treadmill20app.adapters;
/*
Get application context without memory leak
From: https://www.dev2qa.com/android-get-application-context-from-anywhere-example/
*/

import android.app.Application;
import android.content.Context;

public class AppCtx extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}