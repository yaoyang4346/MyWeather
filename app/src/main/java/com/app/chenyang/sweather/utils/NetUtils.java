package com.app.chenyang.sweather.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

import com.app.chenyang.sweather.R;

/**
 * Created by chenyang on 2017/2/15.
 */

public class NetUtils {
    public static boolean isConnected() {
        return getCurrentNetworkState() == NetworkInfo.State.CONNECTED;
    }


    public static NetworkInfo.State getCurrentNetworkState() {
        NetworkInfo networkInfo
                = ((ConnectivityManager) BaseUtils.getContext().getSystemService(
                Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return networkInfo != null ? networkInfo.getState() : null;
    }


}
