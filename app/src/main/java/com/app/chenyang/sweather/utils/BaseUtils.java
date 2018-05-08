package com.app.chenyang.sweather.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.app.chenyang.sweather.global.MyApplication;
import com.app.chenyang.sweather.global.MyConst;
import com.jakewharton.rxbinding.view.RxView;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import rx.functions.Action1;

import static java.lang.System.currentTimeMillis;

/**
 * Created by chenyang on 2017/2/10.
 */

public class BaseUtils {
    private static Toast toast = null;
    private static final int HOUR = 60 * 60 * 1000;
    public static final DisplayMetrics DM = getContext().getResources().getDisplayMetrics();
    public static final float SCREEN_DENSITY = DM.density;
    public static final float SCREEN_WIDTH = DM.widthPixels;
    public static final float SCREEN_HEIGHT = DM.heightPixels;

    public static Context getContext() {
        return MyApplication.getContext();
    }

    public static int getMainThreadId(){
        return MyApplication.getMainThread();
    }

    public static boolean isRunOnUiThread() {
        return getMainThreadId() == android.os.Process.myTid();
    }
    public static Handler getHandler() {
        return MyApplication.getHandler();
    }

    public static void showToast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(getContext(), str, Toast.LENGTH_SHORT);
                }
                else {
                    toast.setText(str);
                }
                toast.show();
            }
        });
    }

    public static void showToast(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT);
                }
                else {
                    toast.setText(resId);
                }
                toast.show();
            }
        });
    }

    public static void clickEvent(View v, Action1<Void> action1){
        RxView.clicks(v)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(action1);
    }

    public static void runOnUiThread(Runnable runnable) {
        if (isRunOnUiThread()) {
            runnable.run();
        } else {
            getHandler().post(runnable);
        }
    }

    public static int getImgResourceByName(String name){
        return getContext().getResources().getIdentifier(name, "mipmap", getContext().getPackageName());
    }

    public static String getString(int res){
        return getContext().getString(res);
    }

    public static int dpToPx(double dp) {
        return (int) (dp * SCREEN_DENSITY + 0.5f);
    }

}
