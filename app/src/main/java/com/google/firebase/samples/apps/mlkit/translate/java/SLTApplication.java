package com.google.firebase.samples.apps.mlkit.translate.java;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class SLTApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context mContex;

    @Override
    public void onCreate() {
        super.onCreate();
        mContex=this;
    }

    public static Context getmContex()
    {
        return mContex;
    }
}
