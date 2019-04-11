package com.jhj.adapterdemo;

import android.app.Application;

import com.jhj.httplibrary.HttpCall;


/**
 * Created by jhj on 18-10-22.
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        HttpCall.INSTANCE.init(this);
    }
}
