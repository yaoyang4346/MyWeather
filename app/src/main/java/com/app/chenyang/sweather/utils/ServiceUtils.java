package com.app.chenyang.sweather.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.db.WeatherDao;
import com.app.chenyang.sweather.entity.CityPosition;
import com.app.chenyang.sweather.entity.HeWeather;
import com.app.chenyang.sweather.entity.RefreshEvent;
import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.global.MyConst;
import com.app.chenyang.sweather.network.WeatherRequest;
import com.app.chenyang.sweather.service.UpdateWeatherService;
import com.app.chenyang.sweather.ui.fragment.SettingsFragment;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import rx.functions.Action1;

/**
 * Created by chenyang on 2017/5/5.
 */

public class ServiceUtils {
    public static final int TWENTY_MINUTE = 20 * 60 * 1000;
    public static final int ONE_HOUR  = 60 * 60 * 1000;
    public static final int OPEN_GPS = 66;
    private static AMapLocationClient locationClient;
    private static AMapLocationClientOption locationClientOption;

    public static void cancelAlarm(AlarmManager am){
        Intent intent = new Intent(MyConst.UPDATE_WEATHER_ALARM_ACTION);
        intent.setClass(BaseUtils.getContext(), UpdateWeatherService.class);
        PendingIntent pendingIntent = PendingIntent.getService(BaseUtils.getContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
        if(pendingIntent != null){
            LogUtils.d("cancel alarm...");
            am.cancel(pendingIntent);
        }
    }

    public static void setAlarm(AlarmManager am,boolean isRetry){
        cancelAlarm(am);
        Intent intent = new Intent(MyConst.UPDATE_WEATHER_ALARM_ACTION);
        intent.setClass(BaseUtils.getContext(), UpdateWeatherService.class);
        PendingIntent pendingIntent = PendingIntent.getService(BaseUtils.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long intervalTime = 0;
        if(isRetry){
            intervalTime = TWENTY_MINUTE ;
        }else{
            int auto = getAutoTime();
            if(auto == 0){
                LogUtils.d("no auto refresh!");
                return;
            }
            intervalTime = getAutoTime() * ONE_HOUR ;
        }
        if(Build.VERSION.SDK_INT >= 23){
            am.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,intervalTime + SystemClock.elapsedRealtime(),pendingIntent);
        }else {
            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,intervalTime + SystemClock.elapsedRealtime(),pendingIntent);
        }

        LogUtils.d("set new alarm... next time:" + intervalTime+"sdk version : " + Build.VERSION.SDK_INT);
    }

    public static int getAutoTime(){
        String timeStr = PreferenceManager.getDefaultSharedPreferences(BaseUtils.getContext()).getString(SettingsFragment.AUTO_REFRESH,"3");
        int time = 3;
        try{
            time = Integer.parseInt(timeStr);
        }catch (Exception e){
            e.printStackTrace();
        }
        return time;
    }

    public static boolean isUpdateOverTimeException(){
        if(Math.abs(SystemClock.elapsedRealtime() - PrefUtils.getLong(PrefUtils.IS_UPDATE,-1)) > (TWENTY_MINUTE * 0.075)){
            PrefUtils.putLong(PrefUtils.IS_UPDATE,-1);
            return true;
        }
        return false;
    }

    public static void sendEventUpdateWidget(int position){
        if(PrefUtils.getBoolean(PrefUtils.HAS_WIDGET,false)){
            LogUtils.d("Widget exists, update widget");
            if(position != -1){
                PrefUtils.putInt(PrefUtils.CURRENT_CITY,position);
            }
            BaseUtils.getContext().sendBroadcast(new Intent(MyConst.CURRENT_CITY_CHANGE));
        }
    }

    public static void getLocation(final boolean isUser){
        LogUtils.d("start getLocation");
        PrefUtils.putLong(PrefUtils.IS_LOCATION,SystemClock.elapsedRealtime());
        if(isUser){
            BaseUtils.showToast(R.string.positioning);
        }
        locationClient = new AMapLocationClient(BaseUtils.getContext());
        locationClientOption = new AMapLocationClientOption();

        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null){
                    int errorCode = aMapLocation.getErrorCode();
                    switch (errorCode){
                        case AMapLocation.LOCATION_SUCCESS:
                            PrefUtils.putLong(PrefUtils.IS_LOCATION,SystemClock.elapsedRealtime());
                            if (isUser){
                                BaseUtils.showToast(R.string.location__success);
                            }
                            LogUtils.d("location success , refresh data , Latitude:" + aMapLocation.getLatitude() + ",Longitude:" + aMapLocation.getLongitude());
                            WeatherRequest.getInstance().getWeather(
                                    new Action1<HeWeather>() {
                                        @Override
                                        public void call(HeWeather heWeather) {
                                            HeWeather.HeWeather5Bean heWeather5Bean = heWeather.getHeWeather5().get(0);
                                            switch (heWeather5Bean.getStatus()) {
                                                case MyConst.OK:
                                                    PrefUtils.putLong(PrefUtils.IS_LOCATION,-1);
                                                    BaseUtils.showToast(R.string.refresh_success);
                                                    WeatherDataInfo gpsData = WeatherDao.getGPSData();
                                                    boolean isFirst = gpsData == null;
                                                    int pos = -1;
                                                    if (gpsData != null){
                                                        pos = gpsData.getPosition();
                                                    }
                                                    if (WeatherDao.saveWeatherData(heWeather5Bean, pos ,true)){
                                                        if (DataSupport.count(WeatherDataInfo.class) == 1) {
                                                            LogUtils.d("add first city,activate alarm");
                                                            ServiceUtils.setAlarm((AlarmManager) BaseUtils.getContext().getSystemService(Context.ALARM_SERVICE), false);
                                                            ServiceUtils.sendEventUpdateWidget(0);
                                                        }

                                                        if (isUser){
                                                            LogUtils.d("user get location success");
                                                            EventBus.getDefault().removeStickyEvent(CityPosition.class);
                                                            EventBus.getDefault().postSticky(new CityPosition(WeatherDao.getGPSData().getPosition()));
                                                        }else{
                                                            LogUtils.d("auto get location success");
                                                            ServiceUtils.sendEventUpdateWidget(-1);
                                                            EventBus.getDefault().removeStickyEvent(RefreshEvent.class);
                                                            EventBus.getDefault().postSticky(new RefreshEvent(true));
                                                        }
                                                    }else{
                                                        LogUtils.e("save db fail (getPosition)");
                                                    }
                                                    break;
                                                default:
                                                    PrefUtils.putLong(PrefUtils.IS_LOCATION,-1);
                                                    BaseUtils.showToast(R.string.refresh_fail);
                                                    LogUtils.e("get location weather fail"+heWeather5Bean.getStatus());
                                                    break;
                                            }
                                        }
                                    }
                                    , new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            PrefUtils.putLong(PrefUtils.IS_LOCATION,-1);
                                            BaseUtils.showToast(R.string.refresh_fail);
                                            LogUtils.e("get location weather fail"+throwable.getMessage());
                                            throwable.printStackTrace();
                                        }
                                    }
                                    , aMapLocation.getLongitude()+","+aMapLocation.getLatitude());
                            break;
                        case AMapLocation.ERROR_CODE_FAILURE_LOCATION_PERMISSION:
                            PrefUtils.putLong(PrefUtils.IS_LOCATION,-1);
                            LogUtils.e("error code " + errorCode + ",error info " + aMapLocation.getErrorInfo());
                            if (isUser){
                                BaseUtils.showToast(R.string.fail_location_permission);
                            }
                            break;
                        default:
                            PrefUtils.putLong(PrefUtils.IS_LOCATION,-1);
                            LogUtils.e("error code " + errorCode + ",error info " + aMapLocation.getErrorInfo());
                            if (isUser){
                                BaseUtils.showToast(R.string.fail_location);
                            }
                            break;
                    }
                }
                locationClient.stopLocation();
                locationClient.onDestroy();
            }
        });
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClientOption.setOnceLocationLatest(true);
        locationClientOption.setNeedAddress(true);
        locationClient.setLocationOption(locationClientOption);
        locationClient.startLocation();
    }

    public static boolean GPSHelp(final Activity activity){
        LocationManager locationManager = (LocationManager) BaseUtils.getContext().getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(R.string.gps_help_msg);
            builder.setPositiveButton(R.string.open, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),OPEN_GPS);
                }
            });
            builder.setNegativeButton(R.string.later_on, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getLocation(true);
                }
            });
            builder.show();
            return false;
        }
        return true;
    }

    public static boolean isLocationOverTimeException(){
        if(Math.abs(SystemClock.elapsedRealtime() - PrefUtils.getLong(PrefUtils.IS_LOCATION,-1)) > (TWENTY_MINUTE * 0.075)){
            PrefUtils.putLong(PrefUtils.IS_LOCATION,-1);
            return true;
        }
        return false;
    }
}
