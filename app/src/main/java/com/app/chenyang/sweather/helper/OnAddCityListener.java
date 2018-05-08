package com.app.chenyang.sweather.helper;

import com.app.chenyang.sweather.entity.HeWeather;
import com.app.chenyang.sweather.entity.SearchCityInfo;

import java.util.ArrayList;

/**
 * Created by chenyang on 2017/3/31.
 */

public interface OnAddCityListener {
    void onIdle();
    void onLoading();
    void onNullSearch();
    void onSearchSuccess(ArrayList<SearchCityInfo> cityList,String key);
    void onLoadWeatherSuccess(int mode);
    void onNetError();
    void onServeError();
    void onWindowClick(int position);
}
