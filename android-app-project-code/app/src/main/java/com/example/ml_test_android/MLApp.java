package com.example.ml_test_android;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class MLApp extends Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!

    private static MLApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        instance = this;
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static MLApp getInstance() {
        return instance;
    }

}