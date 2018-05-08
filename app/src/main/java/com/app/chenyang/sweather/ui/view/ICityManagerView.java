package com.app.chenyang.sweather.ui.view;

import com.app.chenyang.sweather.entity.WeatherDataInfo;

import java.util.ArrayList;

/**
 * Created by chenyang on 2017/4/20.
 */

public interface ICityManagerView {
    void showLoading();
    void showAllCity(ArrayList<WeatherDataInfo> allCity);
    void showNull();
    void doFinish();
    void switchMode(int mode);
    void showUpdataLoading();
    void hideUpdataLoading(int msg);
    void updataFail();
    void checkChange();
}
