package com.app.chenyang.sweather.helper;

import com.app.chenyang.sweather.entity.WeatherDataInfo;

import java.util.Comparator;

/**
 * Created by chenyang on 2017/4/14.
 */

public class CityManagerComparator implements Comparator<WeatherDataInfo>{
    @Override
    public int compare(WeatherDataInfo o1, WeatherDataInfo o2) {
        if(o1.getPosition() > o2.getPosition()){
            return 1;
        }
        return -1;
    }
}
