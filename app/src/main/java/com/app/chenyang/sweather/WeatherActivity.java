package com.app.chenyang.sweather;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;

import com.app.chenyang.sweather.adapter.WeatherViewPagerAdapter;
import com.app.chenyang.sweather.entity.CityPosition;
import com.app.chenyang.sweather.entity.PositionChangeEvent;
import com.app.chenyang.sweather.entity.RefreshEvent;
import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.service.UpdateWeatherService;
import com.app.chenyang.sweather.ui.fragment.SettingsFragment;
import com.app.chenyang.sweather.utils.BaseUtils;
import com.app.chenyang.sweather.utils.FileUtils;
import com.app.chenyang.sweather.utils.LogUtils;
import com.app.chenyang.sweather.utils.PrefUtils;
import com.app.chenyang.sweather.utils.ServiceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WeatherActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    public static final String DB_NAME = "weathercity.db";
    @BindView(R.id.vp_main) ViewPager vpMain;

    private WeatherViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d("main activity create");
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);
        initView();
        initDBData();
    }
    private void initView(){
        BaseUtils.clickEvent(findViewById(R.id.tv_add), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(BaseUtils.getContext(),AddCityActivity.class));
            }
        });
        BaseUtils.clickEvent(findViewById(R.id.tv_city), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(BaseUtils.getContext(),CityManageActivity.class));
            }
        });
        BaseUtils.clickEvent(findViewById(R.id.tv_more), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showPopupMenu();
            }
        });
        vpMain.addOnPageChangeListener(this);
        initViewPager();
        if(DataSupport.count(WeatherDataInfo.class) == 0){
            BaseUtils.showToast(R.string.msg_add_city);
            startActivity(new Intent(BaseUtils.getContext(),AddCityActivity.class));
        }
    }

    private void initViewPager(){
        if(adapter==null){
            adapter = new WeatherViewPagerAdapter(getSupportFragmentManager());
            vpMain.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
        vpMain.setCurrentItem(PrefUtils.getInt(PrefUtils.CURRENT_CITY,0));

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        LogUtils.d("weather_activity register event...");

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        LogUtils.d("weather_activity unregister event...");
    }

    private void initDBData() {
        final File dbFile = new File(getFilesDir(), DB_NAME);
        if(dbFile.exists()){
            LogUtils.d("file exits !");
            return;
        }
        LogUtils.d("copy db file !");
        Observable.just(DB_NAME)
                .observeOn(Schedulers.io())
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return FileUtils.copyDB(s,dbFile);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if(!aBoolean){
                            BaseUtils.showToast(R.string.db_copy_fail);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            return;
                        }
                        BaseUtils.showToast(R.string.db_copy_success);
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.POSTING,sticky = true)
    public void getCityPosition(CityPosition position){
        adapter.notifyDataSetChanged();
        vpMain.setCurrentItem(position.getPosition());
        EventBus.getDefault().removeStickyEvent(CityPosition.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void doRefreshPage(RefreshEvent event){
        EventBus.getDefault().removeStickyEvent(RefreshEvent.class);
        LogUtils.d("view pager prepare refresh,because weather data change");
        refreshPage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void doRefreshPageOfPosition(PositionChangeEvent event){
        if (event.isChange()){
            EventBus.getDefault().removeStickyEvent(PositionChangeEvent.class);
            LogUtils.d("view pager prepare refresh,because page position change");
            adapter.notifyDataSetChanged();
        }
    }

    private void showPopupMenu(){
        final PopupMenu popupMenu = new PopupMenu(this,findViewById(R.id.tv_more), Gravity.RIGHT | Gravity.TOP);
        popupMenu.getMenuInflater().inflate(R.menu.weather_menu,popupMenu.getMenu());
        if (!PreferenceManager.getDefaultSharedPreferences(BaseUtils.getContext()).getBoolean(SettingsFragment.GPS,false)){
            popupMenu.getMenu().removeItem(R.id.location);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.contact:
                        break;
                    case R.id.setting:
                        startActivity(new Intent(BaseUtils.getContext(),SettingsActivity.class));
                        break;
                    case R.id.update:
                        if(PrefUtils.getLong(PrefUtils.IS_UPDATE,-1) == -1 || ServiceUtils.isUpdateOverTimeException()){
                            if(DataSupport.count(WeatherDataInfo.class) > 0){
                                UpdateWeatherService.startService(BaseUtils.getContext(),true);
                            }else{
                                BaseUtils.showToast(R.string.no_city);
                            }
                        }else{
                            BaseUtils.showToast(R.string.update_service_running);
                        }
                        break;
                    case R.id.location:
                        if (PrefUtils.getLong(PrefUtils.IS_LOCATION,-1) == -1 || ServiceUtils.isLocationOverTimeException()){
                            if(ServiceUtils.GPSHelp(WeatherActivity.this)){
                                ServiceUtils.getLocation(true);
                            }
                        }else{
                            BaseUtils.showToast(R.string.location_service_running);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void refreshPage(){
        int currentPos = adapter.getCurrentFragment().getCurrentPosition();
        int previousPos = currentPos - 1;
        int nextPos = currentPos + 1;
        LogUtils.d("currentPos:"+currentPos+"  previousPos:"+previousPos+"  nextPos:"+nextPos);
        if(previousPos != -1){
            adapter.getItem(previousPos).refreshData();
        }

        adapter.getItem(currentPos).refreshData();

        if(nextPos <= adapter.getCount() - 1){
            adapter.getItem(nextPos).refreshData();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ServiceUtils.sendEventUpdateWidget(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ServiceUtils.OPEN_GPS){
            ServiceUtils.getLocation(true);
        }
    }
}
