package com.app.chenyang.sweather.presenter;

import android.widget.EditText;

import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.db.WeatherDao;
import com.app.chenyang.sweather.entity.SearchCityInfo;
import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.model.AddCityModel;
import com.app.chenyang.sweather.helper.OnAddCityListener;
import com.app.chenyang.sweather.ui.view.AddCityViewManager;
import com.app.chenyang.sweather.ui.view.IAddCityView;
import com.app.chenyang.sweather.utils.BaseUtils;
import com.app.chenyang.sweather.utils.LogUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

import rx.Subscription;

/**
 * Created by chenyang on 2017/3/31.
 */

public class AddCityPresenter extends BasePresenter<IAddCityView> implements OnAddCityListener{
    private IAddCityView cityView;
    private AddCityModel cityModel;
    private Subscription searchSubscription;
    private Subscription loadSubscription;

    public AddCityPresenter(IAddCityView cityView){
        this.cityView = cityView;
        cityModel = new AddCityModel(this);
    }

    public void searchCity(EditText editText){
        LogUtils.d("subscribe searchSubscription");
        searchSubscription = cityModel.searchCity(editText);
        rxList.add(searchSubscription);
    }

    public void loadWeather(String cityID,int mode){
        if(WeatherDao.isExist(cityID) == WeatherDao.EXIST){
            onIdle();
            BaseUtils.showToast(R.string.registered);
            return;
        }
        LogUtils.d("subscribe loadSubscription");
        loadSubscription = cityModel.loadWeather(cityID,mode);
        rxList.add(loadSubscription);
    }

    public void unsubscribeload(){
        if (loadSubscription != null && ! loadSubscription.isUnsubscribed()){
            loadSubscription.unsubscribe();
            LogUtils.d("unsubscribe loadSubscription");
        }
    }

    public void loadCityAtMap(MapView mapView){
        cityModel.showCityAtMap(mapView);
    }

    @Override
    public void onIdle() {
        cityView.showIdle();
    }

    @Override
    public void onLoading() {
        cityView.showLoading();
    }

    @Override
    public void onNullSearch() {
        cityView.showNullSearch();
    }

    @Override
    public void onSearchSuccess(ArrayList<SearchCityInfo> cityList, String key) {
        cityView.showSearchSuccess(cityList,key);
    }

    @Override
    public void onLoadWeatherSuccess(int mode) {
        if (mode == AddCityViewManager.TEXT_MODE){
            cityView.loadSuccess();
            return;
        }
        cityView.showIdle();
        WeatherDataInfo weatherDataInfo = WeatherDao.getDataByPosition(DataSupport.count(WeatherDataInfo.class) - 1);
        cityModel.showNewCityAtMap(weatherDataInfo);
    }

    @Override
    public void onNetError() {
        cityView.showNetError();
    }

    @Override
    public void onServeError() {
        cityView.showServeError();
    }

    @Override
    public void onWindowClick(int position) {
        cityView.jumpTargetPage(position);
    }

}
