package com.hardbacknutter.sshd;

import android.app.Application;

public class App
        extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        NightMode.apply(this);
    }
}
