package com.tealium.example;

import android.app.Application;

import com.tealium.example.helper.TealiumHelper;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TealiumHelper.initialize(this);
    }
}
