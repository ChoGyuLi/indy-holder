package com.example.app;

import android.app.Application;

import kr.co.bdgen.indywrapper.IndyWrapper;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        IndyWrapper.init(this);

    }
}
