package com.app.chenyang.sweather.receiver;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.chenyang.sweather.utils.LogUtils;
import com.app.chenyang.sweather.utils.ServiceUtils;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d("boot receiver,reset alarm");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        ServiceUtils.setAlarm(am,false);
    }
}
