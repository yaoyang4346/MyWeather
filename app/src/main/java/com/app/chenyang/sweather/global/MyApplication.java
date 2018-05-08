package com.app.chenyang.sweather.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.app.chenyang.sweather.MyEventBusIndex;
import com.facebook.stetho.Stetho;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusBuilder;
import org.litepal.LitePalApplication;

/**
 * Created by chenyang on 2017/2/10.
 */

public class MyApplication extends LitePalApplication {
    private static Context mContext;
    private static int mainThread;
    private static Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
        Stetho.initializeWithDefaults(this);
        mContext = getApplicationContext();
        mainThread = android.os.Process.myTid();
        handler = new Handler();
    }
    public static Context getContext(){
        return mContext;
    }
    public static int getMainThread(){
        return mainThread;
    }
    public static Handler getHandler() {
        return handler;
    }
}
