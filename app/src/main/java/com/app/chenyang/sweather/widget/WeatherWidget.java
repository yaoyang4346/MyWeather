package com.app.chenyang.sweather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;

import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.WeatherActivity;
import com.app.chenyang.sweather.db.WeatherDao;
import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.global.MyConst;
import com.app.chenyang.sweather.utils.BaseUtils;
import com.app.chenyang.sweather.utils.LogUtils;
import com.app.chenyang.sweather.utils.PrefUtils;

import org.litepal.crud.DataSupport;

public class WeatherWidget extends AppWidgetProvider {
    private AppWidgetManager mAppWidgetManager;
    private int[] mAppWidgetIds;
    private Drawable locationIcon;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        LogUtils.d("widget update,refresh data");
        updateWidget(context,appWidgetManager,appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        init();
        switch (intent.getAction()){
            case MyConst.CURRENT_CITY_CHANGE:
                LogUtils.d("receive city change broadcast,widget update");
                updateWidget(context,mAppWidgetManager,mAppWidgetIds);
                break;
            default:
                break;
        }
    }

    @Override
    public void onEnabled(Context context) {
        LogUtils.d("The first widget is added ");
        PrefUtils.putBoolean(PrefUtils.HAS_WIDGET,true);
    }

    @Override
    public void onDisabled(Context context) {
        LogUtils.d("All widgets are removed ");
        PrefUtils.putBoolean(PrefUtils.HAS_WIDGET,false);
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        WeatherDataInfo weatherDataInfo = WeatherDao.getDataByPosition(PrefUtils.getInt(PrefUtils.CURRENT_CITY,0));
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
            if (weatherDataInfo != null){
                if (weatherDataInfo.isGps()){
                    views.setImageViewResource(R.id.location,R.drawable.ic_location_on_black);
                }else{
                    views.setImageViewResource(R.id.location,R.drawable.translate_icon);
                }
                views.setTextViewText(R.id.tv_temp, weatherDataInfo.getTmp());
                views.setImageViewResource(R.id.iv_icon,weatherDataInfo.getIcon());
                views.setTextViewText(R.id.tv_city,weatherDataInfo.getName());
                views.setTextViewText(R.id.tv_weather_aqi,weatherDataInfo.getAqi());
                views.setTextViewText(R.id.tv_time,weatherDataInfo.getServeTime());
            }else{
                views.setImageViewResource(R.id.location,R.drawable.translate_icon);
                views.setTextViewText(R.id.tv_temp, "");
                views.setImageViewResource(R.id.iv_icon,R.mipmap.s999);
                views.setTextViewText(R.id.tv_city,"");
                views.setTextViewText(R.id.tv_weather_aqi,BaseUtils.getString(R.string.select_city));
                views.setTextViewText(R.id.tv_time,"");
            }
            setClickEvent(views);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void setClickEvent(RemoteViews views){
        Intent weatherIntent = new Intent(BaseUtils.getContext(),WeatherActivity.class);

        PendingIntent weatherPending = PendingIntent.getActivity(BaseUtils.getContext(), 0, weatherIntent, 0);
        views.setOnClickPendingIntent(R.id.ll_widget, weatherPending);
    }

    private void init(){
        if (mAppWidgetManager == null) {
            mAppWidgetManager = AppWidgetManager.getInstance(BaseUtils
                    .getContext());
        }
        mAppWidgetIds = mAppWidgetManager
                .getAppWidgetIds(new ComponentName(BaseUtils.getContext(),
                        WeatherWidget.class));
    }
}

