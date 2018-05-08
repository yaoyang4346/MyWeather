package com.app.chenyang.sweather.presenter;

import android.content.Intent;

import com.app.chenyang.sweather.adapter.CityManagerAdapter;
import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.global.MyConst;
import com.app.chenyang.sweather.helper.OnCityManagerListener;
import com.app.chenyang.sweather.model.CityManagerModel;
import com.app.chenyang.sweather.ui.view.ICityManagerView;
import com.app.chenyang.sweather.utils.BaseUtils;
import com.app.chenyang.sweather.utils.LogUtils;
import com.app.chenyang.sweather.utils.PrefUtils;
import com.app.chenyang.sweather.utils.ServiceUtils;

import java.util.ArrayList;

/**
 * Created by chenyang on 2017/4/20.
 */

public class CityManagerPresenter extends BasePresenter<ICityManagerView> implements OnCityManagerListener{
    private ICityManagerView cityManagerView;
    private CityManagerModel cityManagerModel;

    public CityManagerPresenter(ICityManagerView cityManagerView){
        this.cityManagerView = cityManagerView;
        cityManagerModel = new CityManagerModel(this);
    }

    public void loadAllCity(){
        rxList.add(cityManagerModel.loadAllCity());
    }

    public void recycleClickEvent(int position, int mode, CityManagerAdapter adapter){
        cityManagerModel.doClickEvent(position,mode,adapter);
    }

    public void recycleLongPressEvent(int mode,CityManagerAdapter adapter){
        cityManagerModel.doLongPressEvent(mode,adapter);
    }

    public void clickBack(int mode,CityManagerAdapter adapter){
        cityManagerModel.doClickBack(mode,adapter);
    }

    public void revoceryDB(CityManagerAdapter adapter){
        cityManagerModel.doRecoveryDB(adapter);
    }

    public void deleteCity(CityManagerAdapter adapter){
        cityManagerModel.deleteCity(adapter);
    }

    public void checkAll(Boolean isCheck,CityManagerAdapter adapter){
        cityManagerModel.doCheckAll(isCheck,adapter);
    }

    @Override
    public void onLoading() {
        cityManagerView.showLoading();
    }

    @Override
    public void onLoadSuccess(ArrayList<WeatherDataInfo> allCity) {
        cityManagerView.showAllCity(allCity);
    }

    @Override
    public void onLoadNull() {
        cityManagerView.showNull();
    }

    @Override
    public void onFinish() {
        cityManagerView.doFinish();
    }

    @Override
    public void onSwitchMode(int mode) {
        cityManagerView.switchMode(mode);
    }

    @Override
    public void onUpdataDBStart() {
        cityManagerView.showUpdataLoading();
    }

    @Override
    public void onUpdataDBComplete(int msg) {
        ServiceUtils.sendEventUpdateWidget(-1);
        cityManagerView.hideUpdataLoading(msg);
    }

    @Override
    public void onUpdataDBFail() {
        cityManagerView.updataFail();
    }

    @Override
    public void onCheckChange() {
        cityManagerView.checkChange();
    }
}
