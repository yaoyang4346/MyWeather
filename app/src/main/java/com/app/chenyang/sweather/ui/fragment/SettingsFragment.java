package com.app.chenyang.sweather.ui.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;

import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.db.WeatherDao;
import com.app.chenyang.sweather.entity.PositionChangeEvent;
import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.utils.BaseUtils;
import com.app.chenyang.sweather.utils.PrefUtils;
import com.app.chenyang.sweather.utils.ServiceUtils;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

/**
 * Created by chenyang on 2017/5/3.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    public static final String AUTO_REFRESH = "auto_refresh";
    public static final String GPS = "gps";
    private PreferenceScreen root;
    private ListPreference autoRefresh;
    private ProgressDialog dialog;
    private CheckBoxPreference gps;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        root = getPreferenceScreen();
        root.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        autoRefresh = (ListPreference) findPreference(AUTO_REFRESH);
        autoRefresh.setSummary(autoRefresh.getEntry());
        gps = (CheckBoxPreference) findPreference(GPS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        root.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (GPS.equals(preference.getKey())){
            if(gps.isChecked()){
                if (ServiceUtils.GPSHelp(getActivity())){
                    ServiceUtils.getLocation(true);
                }
            }else{
                if (PrefUtils.getLong(PrefUtils.IS_LOCATION,-1) == -1 || ServiceUtils.isLocationOverTimeException()){
                    new CleanTask().execute(null,null,null);
                }else{
                    BaseUtils.showToast(R.string.location_service_running);
                    gps.setChecked(true);
                }
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key){
            case AUTO_REFRESH:
                autoRefresh.setSummary(autoRefresh.getEntry());
                break;
            default:
                break;
        }
    }



    private void triggerDialog(boolean isShow){
        if (isShow){
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage(getString(R.string.clear_location));
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
            return;
        }
        if (dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }

    class CleanTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            triggerDialog(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            DataSupport.deleteAll(WeatherDataInfo.class , WeatherDao.COLUMN_GPS + " = 1");
            int i = 0;
            for (WeatherDataInfo weatherDataInfo : DataSupport.findAll(WeatherDataInfo.class)){
                weatherDataInfo.setPosition(i);
                weatherDataInfo.saveOrUpdate(WeatherDao.COLUMN_AREA_ID + " = ?" , weatherDataInfo.getAreaId());
                ++i;
            }
            FragmentFactory.getInstance().deleteFragment();
            EventBus.getDefault().postSticky(new PositionChangeEvent(true));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            triggerDialog(false);
        }
    }
}
