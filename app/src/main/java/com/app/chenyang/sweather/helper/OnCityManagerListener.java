package com.app.chenyang.sweather.helper;

import com.app.chenyang.sweather.entity.WeatherDataInfo;

import java.util.ArrayList;

/**
 * Created by chenyang on 2017/4/20.
 */

public interface OnCityManagerListener {
    void onLoading();
    void onLoadSuccess(ArrayList<WeatherDataInfo> allCity);
    void onLoadNull();
    void onFinish();
    void onSwitchMode(int mode);
    void onUpdataDBStart();
    void onUpdataDBComplete(int msg);
    void onUpdataDBFail();
    void onCheckChange();
}
