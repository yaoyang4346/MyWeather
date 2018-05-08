package com.app.chenyang.sweather.ui.fragment;

import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.utils.LogUtils;

import org.litepal.crud.DataSupport;

import java.util.HashMap;

/**
 * Created by chenyang on 2017/2/16.
 */

public class FragmentFactory {

    private static HashMap<Integer, BaseFragment> mFragmentMap = new HashMap<Integer, BaseFragment>();

    private FragmentFactory(){}

    private static class SingletonFactory{
        public static final FragmentFactory instance = new FragmentFactory();
    }

    public static FragmentFactory getInstance(){
        return SingletonFactory.instance;
    }

    public BaseFragment createFragment(int position){
        BaseFragment fragment = mFragmentMap.get(position);
        if (fragment == null) {
            fragment = BaseFragment.newInstance(position);
            mFragmentMap.put(position,fragment);
        }
        return fragment;
    }

    public void deleteFragment(){
        int currentPageNumber = DataSupport.count(WeatherDataInfo.class);
        int savePageNumber = mFragmentMap.size();
        LogUtils.d("Before deleting :: currentPageNumber : "+ currentPageNumber + " savePageNumber : " + mFragmentMap.size());
        for (int i = 0 ; i < savePageNumber - currentPageNumber ; i++){
            mFragmentMap.remove(mFragmentMap.size() - 1);
        }
        LogUtils.d("After deletion :: currentPageNumber : "+ currentPageNumber + " savePageNumber : " + mFragmentMap.size());
    }
}
