package com.app.chenyang.sweather.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.db.WeatherDao;
import com.app.chenyang.sweather.entity.HeWeather;
import com.app.chenyang.sweather.entity.RefreshEvent;
import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.global.MyConst;
import com.app.chenyang.sweather.network.WeatherRequest;
import com.app.chenyang.sweather.utils.BaseUtils;
import com.app.chenyang.sweather.utils.LogUtils;
import com.app.chenyang.sweather.utils.NetUtils;
import com.app.chenyang.sweather.utils.PrefUtils;
import com.app.chenyang.sweather.utils.ServiceUtils;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;

import rx.Subscription;
import rx.functions.Action1;

public class UpdateWeatherService extends IntentService {
    private static final String IS_USER = "isUser";
    private static final int NOTIFICATION_ID = 10;
    private ArrayList<Subscription> allSubscription;
    private boolean failCity = false;
    private AlarmManager am;
    private NotificationManager notificationManager ;

    public UpdateWeatherService() {
        super("UpdateWeatherService");
        if (notificationManager == null){
            notificationManager = (NotificationManager) BaseUtils.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
    }

    public static void startService(Context context,boolean isUser) {
        Intent intent = new Intent(context, UpdateWeatherService.class);
        intent.putExtra(IS_USER,isUser);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PrefUtils.putLong(PrefUtils.IS_UPDATE,SystemClock.elapsedRealtime());
        LogUtils.d("service running...");
        boolean isUser = intent.getBooleanExtra(IS_USER,false);
        init();
        LogUtils.d("update from "+(isUser ? "user" : "auto"));

        ServiceUtils.setAlarm(am,false);

        if(NetUtils.isConnected()){
            LogUtils.d("net connect , start updata");
            if(isUser){
                BaseUtils.showToast(R.string.refresh_weather_data);
            }
            updateWeather();
            if(failCity){
                LogUtils.d("update fail,retry...");
                if(isUser){
                    BaseUtils.showToast(R.string.refresh_fail);
                }else{
                    ServiceUtils.setAlarm(am,true);
                }
            }else{
                LogUtils.d("update success and send event");
                ServiceUtils.sendEventUpdateWidget(-1);
                if(isUser){
                    BaseUtils.showToast(R.string.refresh_success);
                }
                EventBus.getDefault().removeStickyEvent(RefreshEvent.class);
                EventBus.getDefault().postSticky(new RefreshEvent(true));
            }
        }else{
            if (isUser){
                LogUtils.d("no net,show msg");
                BaseUtils.showToast(R.string.no_net_msg);
            }else{
                LogUtils.d("no net,retry...");
                ServiceUtils.setAlarm(am,true);
            }
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        PrefUtils.putLong(PrefUtils.IS_UPDATE,-1);
        for (Subscription subscription : allSubscription){
            if(subscription!=null && !subscription.isUnsubscribed()){
                subscription.unsubscribe();
            }
        }
        LogUtils.d("service onDestroy and unsubscribe");
    }

    private void updateWeather(){
        ArrayList<WeatherDataInfo> allCity = (ArrayList<WeatherDataInfo>) DataSupport.findAll(WeatherDataInfo.class);
        WeatherDataInfo gpsData = null;
        for (WeatherDataInfo weatherDataInfo : allCity){
            if (!weatherDataInfo.isGps()){
                allSubscription.add(loadWeather(weatherDataInfo.getAreaId(),weatherDataInfo.getPosition()));
                if(failCity){
                    break;
                }else{
                    PrefUtils.putLong(PrefUtils.IS_UPDATE,SystemClock.elapsedRealtime());
                }
            }else{
                gpsData = weatherDataInfo;
            }
        }
        if(gpsData != null && !failCity){
            LogUtils.d("start refresh gps data");
            BaseUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ServiceUtils.getLocation(false);
                }
            });
        }
    }

    private void init() {
        if(allSubscription == null){
            allSubscription = new ArrayList<>();
        }else{
            allSubscription.clear();
        }
        if(am == null){
            am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        failCity = false;
    }

    private Subscription loadWeather(final String id, final int pos){
        return WeatherRequest.getInstance().getWeatherNowThread(
                new Action1<HeWeather>() {
                    @Override
                    public void call(HeWeather heWeather) {
                        HeWeather.HeWeather5Bean heWeather5Bean = heWeather.getHeWeather5().get(0);
                        switch (heWeather5Bean.getStatus()){
                            case MyConst.OK:
                                if(!WeatherDao.saveWeatherData(heWeather5Bean,pos,false)){
                                    LogUtils.e("save db fail(refresh data)");
                                    failCity = true;
                                }
                                break;
                            default:
                                LogUtils.d("server fail");
                                failCity = true;
                                break;
                        }
                    }
                }
                , new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.d("net fail");
                        throwable.printStackTrace();
                        failCity = true;
                    }
                }
                , id);
    }


}
