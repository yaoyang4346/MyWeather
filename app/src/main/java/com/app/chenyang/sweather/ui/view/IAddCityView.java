package com.app.chenyang.sweather.ui.view;

import com.app.chenyang.sweather.entity.SearchCityInfo;

import java.util.ArrayList;

/**
 * Created by chenyang on 2017/3/31.
 */

public interface IAddCityView {
    void showIdle();
    void showLoading();
    void showNullSearch();
    void showSearchSuccess(ArrayList<SearchCityInfo> cityList, String key);
    void showNetError();
    void showServeError();
    void loadSuccess();
    void jumpTargetPage(int position);
}
