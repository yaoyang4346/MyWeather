package com.app.chenyang.sweather.utils;

import android.content.Context;

import com.app.chenyang.sweather.global.MyConst;

/**
 * Created by chenyang on 2017/2/15.
 */

public class PrefUtils {
    public static String PREFERENCE_NAME = "SWeatherData";
    public static final String IS_FIRST = "is_first";
    public static final String IS_UPDATE = "is_update";
    public static final String CURRENT_CITY = "current_city";
    public static final String HAS_WIDGET = "has_widget";
    public static final String IS_LOCATION = "is_location";

    public static boolean putBoolean(String key,boolean value){
        return BaseUtils.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(key,value).commit();
    }

    public static boolean getBoolean(String key,boolean defValue){
        return BaseUtils.getContext().getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)
                .getBoolean(key,defValue);
    }

    public static boolean putInt(String key, int value){
        return BaseUtils.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit().putInt(key,value).commit();
    }

    public static int getInt(String key,int defValue){
        return BaseUtils.getContext().getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)
                .getInt(key,defValue);
    }

    public static boolean putLong(String key,long value){
        return BaseUtils.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit().putLong(key,value).commit();
    }

    public static long getLong(String key,int defValue){
        return BaseUtils.getContext().getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)
                .getLong(key,defValue);
    }

    public static boolean isFirst(){
        return getBoolean(IS_FIRST,true);
    }
}
