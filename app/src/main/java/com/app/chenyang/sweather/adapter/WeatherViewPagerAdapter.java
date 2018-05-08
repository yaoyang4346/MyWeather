package com.app.chenyang.sweather.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.ui.fragment.BaseFragment;
import com.app.chenyang.sweather.ui.fragment.FragmentFactory;
import com.app.chenyang.sweather.utils.LogUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * Created by chenyang on 2017/2/16.
 */

public class WeatherViewPagerAdapter extends FragmentStatePagerAdapter {
    private FragmentManager fm;
    private ArrayList<Fragment> fragments;
    private BaseFragment currentFragment;

    public WeatherViewPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @Override
    public BaseFragment getItem(int position) {
        return FragmentFactory.getInstance().createFragment(position);
    }

    @Override
    public int getCount() {
        return DataSupport.count(WeatherDataInfo.class);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentFragment = (BaseFragment) object;
        super.setPrimaryItem(container, position, object);
    }

    public BaseFragment getCurrentFragment(){
        return currentFragment;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

}
