package com.app.chenyang.sweather.model;

import android.app.AlarmManager;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.db.SearchCityDao;
import com.app.chenyang.sweather.db.WeatherDao;
import com.app.chenyang.sweather.entity.HeWeather;
import com.app.chenyang.sweather.entity.SearchCityInfo;
import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.global.MyConst;
import com.app.chenyang.sweather.helper.OnAddCityListener;
import com.app.chenyang.sweather.network.WeatherRequest;
import com.app.chenyang.sweather.utils.BaseUtils;
import com.app.chenyang.sweather.utils.LogUtils;
import com.app.chenyang.sweather.utils.ServiceUtils;
import com.jakewharton.rxbinding.widget.RxTextView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chenyang on 2017/3/31.
 */

public class AddCityModel {
    private String key;
    private OnAddCityListener listener;
    private AMap aMap;

    public AddCityModel(OnAddCityListener listener) {
        this.listener = listener;
    }

    public Subscription searchCity(final EditText editText) {
        return RxTextView.textChanges(editText)
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        return editText.isEnabled();
                    }
                })
                .map(new Func1<CharSequence, CharSequence>() {
                    @Override
                    public CharSequence call(CharSequence charSequence) {
                        if (TextUtils.isEmpty(charSequence)) {
                            listener.onIdle();
                        }
                        return charSequence;
                    }
                })
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        return !TextUtils.isEmpty(charSequence);
                    }
                })
                .observeOn(Schedulers.io())
                .switchMap(new Func1<CharSequence, Observable<ArrayList<SearchCityInfo>>>() {
                    @Override
                    public Observable<ArrayList<SearchCityInfo>> call(CharSequence charSequence) {
                        key = charSequence.toString();
                        BaseUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onLoading();
                            }
                        });
                        return Observable.just(search(key));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<SearchCityInfo>>() {
                    @Override
                    public void call(ArrayList<SearchCityInfo> searchCityInfos) {
                        if (searchCityInfos.size() == 0) {
                            listener.onNullSearch();
                            return;
                        }
                        listener.onSearchSuccess(searchCityInfos, key);
                    }
                });
    }

    public Subscription loadWeather(String cityID, final int mode) {
        listener.onLoading();
        return WeatherRequest.getInstance().getWeather(
                new Action1<HeWeather>() {
                    @Override
                    public void call(HeWeather heWeather) {
                        HeWeather.HeWeather5Bean heWeather5Bean = heWeather.getHeWeather5().get(0);
                        switch (heWeather5Bean.getStatus()) {
                            case MyConst.OK:
                                saveAndDisplayData(heWeather5Bean,mode);
                                break;
                            default:
                                listener.onServeError();
                                break;
                        }
                    }
                }
                , new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        listener.onNetError();
                    }
                }
                , cityID);
    }

    private void saveAndDisplayData(HeWeather.HeWeather5Bean heWeather5Bean,int mode) {
        if (WeatherDao.saveWeatherData(heWeather5Bean, -1 ,false)) {
            if (DataSupport.count(WeatherDataInfo.class) == 1) {
                LogUtils.d("add first city,activate alarm");
                ServiceUtils.setAlarm((AlarmManager) BaseUtils.getContext().getSystemService(Context.ALARM_SERVICE), false);
                ServiceUtils.sendEventUpdateWidget(0);
            }
            listener.onLoadWeatherSuccess(mode);
            return;
        }
        LogUtils.e("save db fail(add city)");
        listener.onServeError();
    }

    public void showCityAtMap(MapView mapView) {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.clear();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(5));
        ArrayList<WeatherDataInfo> allCity = (ArrayList<WeatherDataInfo>) DataSupport.findAll(WeatherDataInfo.class);
        for (WeatherDataInfo weatherDataInfo : allCity) {
            LatLng latLng = new LatLng(weatherDataInfo.getLat(), weatherDataInfo.getLon());
            aMap.addMarker(new MarkerOptions().position(latLng).draggable(false).snippet(""+weatherDataInfo.getPosition())
                    .icon(BitmapDescriptorFactory.fromView(
                                    getInfoWindow(weatherDataInfo.getName(),
                                            weatherDataInfo.getTmp(),
                                            weatherDataInfo.getIcon()))));
        }
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                listener.onWindowClick(Integer.parseInt(marker.getSnippet()));
                return true;
            }
        });
    }

    public void showNewCityAtMap(WeatherDataInfo weatherDataInfo){
        aMap.clear();
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(weatherDataInfo.getLat(),weatherDataInfo.getLon()),10,0,0)));
        LatLng latLng = new LatLng(weatherDataInfo.getLat(), weatherDataInfo.getLon());
        aMap.addMarker(new MarkerOptions().position(latLng).draggable(false).snippet(""+weatherDataInfo.getPosition())
                .icon(BitmapDescriptorFactory.fromView(
                        getInfoWindow(weatherDataInfo.getName(),
                                weatherDataInfo.getTmp(),
                                weatherDataInfo.getIcon()))));
    }

    public View getInfoWindow(String city, String temp, int icon) {
        View view = View.inflate(BaseUtils.getContext(), R.layout.marker_info_layout, null);
        TextView tvCity = (TextView) view.findViewById(R.id.tv_city);
        TextView tvTemp = (TextView) view.findViewById(R.id.tv_temp);
        ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        tvCity.setText(city);
        tvTemp.setText(temp);
        ivIcon.setImageResource(icon);
        return view;
    }

    private ArrayList<SearchCityInfo> search(String key) {
        ArrayList<SearchCityInfo> searchCityList = new ArrayList<>();
        SearchCityDao searchCityDao = new SearchCityDao();
        Cursor cursor = searchCityDao.searchCity(key);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                SearchCityInfo searchCity = new SearchCityInfo();
                searchCity.setName(cursor.getString(0))
                        .setProvince(cursor.getString(1))
                        .setID(cursor.getString(2));
                searchCityList.add(searchCity);
            }
        }
        if (cursor != null) {
            cursor.close();
            searchCityDao.closeDB();
        }
        return searchCityList;
    }
}
