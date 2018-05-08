package com.app.chenyang.sweather.model;

import android.content.Intent;
import android.os.SystemClock;

import com.app.chenyang.sweather.CityManageActivity;
import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.adapter.CityManagerAdapter;
import com.app.chenyang.sweather.db.WeatherDao;
import com.app.chenyang.sweather.entity.CityPosition;
import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.global.MyConst;
import com.app.chenyang.sweather.helper.CityManagerComparator;
import com.app.chenyang.sweather.helper.OnCityManagerListener;
import com.app.chenyang.sweather.ui.fragment.FragmentFactory;
import com.app.chenyang.sweather.utils.BaseUtils;
import com.app.chenyang.sweather.utils.LogUtils;
import com.app.chenyang.sweather.utils.PrefUtils;
import com.app.chenyang.sweather.utils.ServiceUtils;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chenyang on 2017/4/20.
 */

public class CityManagerModel {
    private OnCityManagerListener listener;
    public CityManagerModel(OnCityManagerListener listener){
        this.listener = listener;
    }

    public Subscription loadAllCity(){
        listener.onLoading();
        return Observable.just(1)
                        .observeOn(Schedulers.io())
                        .map(new Func1<Integer, ArrayList<WeatherDataInfo>>() {
                            @Override
                            public ArrayList<WeatherDataInfo> call(Integer integer) {
                                ArrayList<WeatherDataInfo> allCity = (ArrayList<WeatherDataInfo>) DataSupport.findAll(WeatherDataInfo.class);
                                return allCity;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ArrayList<WeatherDataInfo>>() {
                            @Override
                            public void call(ArrayList<WeatherDataInfo> weatherDataInfos) {
                                if(weatherDataInfos != null){
                                    Collections.sort(weatherDataInfos,new CityManagerComparator());
                                    listener.onLoadSuccess(weatherDataInfos);
                                    return;
                                }
                                listener.onLoadNull();
                            }
                        });
    }

    public void doClickEvent(int position, int mode, CityManagerAdapter adapter){
        if(mode == CityManageActivity.NORMAL_MODE){
            CityPosition cityPosition = new CityPosition(position);
            EventBus.getDefault().postSticky(cityPosition);
            listener.onFinish();
            return;
        }
        adapter.adjustCheck(adapter.getAllCity().get(position));
        adapter.notifyItemChanged(position);
        listener.onCheckChange();
    }

    public void doLongPressEvent(int mode,CityManagerAdapter adapter){
        if(PrefUtils.getLong(PrefUtils.IS_UPDATE,-1) == -1 || ServiceUtils.isUpdateOverTimeException()){
            if(mode == CityManageActivity.NORMAL_MODE){
                listener.onSwitchMode(CityManageActivity.EDIT_MODE);
                adapter.setMode(CityManageActivity.EDIT_MODE);
            }
        }else{
            BaseUtils.showToast(R.string.update_service_running);
        }

    }

    public void doClickBack(int mode, CityManagerAdapter adapter){
        if(mode == CityManageActivity.EDIT_MODE){
            listener.onSwitchMode(CityManageActivity.NORMAL_MODE);
            adapter.setMode(CityManageActivity.NORMAL_MODE);
            if(adapter.isPositionChange()){
                adapter.updataChangeState(false);
                ArrayList<WeatherDataInfo> allCity = adapter.getAllCity();
                updataDBPosition(allCity);
            }
            return;
        }
        listener.onFinish();
    }

    public void doRecoveryDB(final CityManagerAdapter adapter){
        Observable.just(1)
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        listener.onUpdataDBStart();
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        ArrayList<WeatherDataInfo> allCity = (ArrayList<WeatherDataInfo>) DataSupport.findAll(WeatherDataInfo.class);
                        int i = 0;
                        boolean updataFlag = true;
                        for(WeatherDataInfo weatherDataInfo : allCity){
                            weatherDataInfo.setPosition(i);
                            if(!weatherDataInfo.saveOrUpdate(WeatherDao.COLUMN_AREA_ID + " = ? and " + WeatherDao.COLUMN_GPS + " = ?" , weatherDataInfo.getAreaId() , weatherDataInfo.isGps()?"1":"0")){
                                updataFlag = false;
                                break;
                            }
                            ++i;
                        }
                        if(!updataFlag){
                            DataSupport.deleteAll(WeatherDataInfo.class);
                        }
                        allCity = (ArrayList<WeatherDataInfo>) DataSupport.findAll(WeatherDataInfo.class);
                        adapter.setAllCity(allCity);
                        SystemClock.sleep(1000);
                        return updataFlag;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        adapter.notifyDataSetChanged();
                        if (aBoolean){
                            listener.onUpdataDBComplete(R.string.recovery_success);
                            return;
                        }
                        listener.onUpdataDBComplete(R.string.delete_all);
                    }
                });
    }

    public void updataDBPosition(ArrayList<WeatherDataInfo> allcity){
        Observable.just(allcity)
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        listener.onUpdataDBStart();
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Func1<ArrayList<WeatherDataInfo>, Boolean>() {
                    @Override
                    public Boolean call(ArrayList<WeatherDataInfo> weatherDataInfos) {
                        int i = 0;
                        boolean updataFlag = true;
                        for(WeatherDataInfo weatherDataInfo : weatherDataInfos){
                            weatherDataInfo.setPosition(i);
                            if(!weatherDataInfo.saveOrUpdate(WeatherDao.COLUMN_AREA_ID + " = ? and " + WeatherDao.COLUMN_GPS + " = ?" , weatherDataInfo.getAreaId() , weatherDataInfo.isGps()?"1":"0")){
                                updataFlag = false;
                                break;
                            }
                            ++i;
                        }
                        SystemClock.sleep(1000);
                        return updataFlag;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean){
                            listener.onUpdataDBComplete(R.string.operate_success);
                            return;
                        }
                        listener.onUpdataDBFail();
                    }
                });
    }

    public void deleteCity(final CityManagerAdapter adapter){
        if(adapter.getCheckList().size() == 0){
            return;
        }
        Observable.just(adapter)
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        listener.onUpdataDBStart();
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Func1<CityManagerAdapter, Boolean>() {
                    @Override
                    public Boolean call(CityManagerAdapter adapter1) {
                        adapter1.deleteCityFromList();
                        for (WeatherDataInfo weatherDataInfo : adapter1.getCheckList()){
                            DataSupport.deleteAll(WeatherDataInfo.class,WeatherDao.COLUMN_AREA_ID + " = ? and " + WeatherDao.COLUMN_GPS + " = 0" , weatherDataInfo.getAreaId());
                        }
                        int i = 0;
                        for(WeatherDataInfo weatherDataInfo : adapter1.getAllCity()){
                            weatherDataInfo.setPosition(i);
                            weatherDataInfo.saveOrUpdate(WeatherDao.COLUMN_AREA_ID + " = ? and " + WeatherDao.COLUMN_GPS + " = ?" , weatherDataInfo.getAreaId() , weatherDataInfo.isGps()?"1":"0");
                            ++i;
                        }
                        FragmentFactory.getInstance().deleteFragment();
                        adapter.updataChangeState(false);

                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        adapter.clearCheckList();
                        listener.onCheckChange();
                        adapter.notifyDataSetChanged();
                        listener.onUpdataDBComplete(R.string.operate_success);
                    }
                });
    }

    public void doCheckAll(boolean isCheck,CityManagerAdapter adapter){
        if(isCheck){
            adapter.addAllToCheckList();
            adapter.notifyDataSetChanged();
            listener.onCheckChange();
            return;
        }
        adapter.clearCheckList();
        adapter.notifyDataSetChanged();
        listener.onCheckChange();
    }
}
