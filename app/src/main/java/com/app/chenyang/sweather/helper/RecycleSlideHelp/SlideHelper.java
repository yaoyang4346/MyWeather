package com.app.chenyang.sweather.helper.RecycleSlideHelp;

import com.app.chenyang.sweather.adapter.viewholder.CityManagerViewHolder;
import com.app.chenyang.sweather.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by chenyang on 2017/4/24.
 */

public class SlideHelper {
    private ArrayList<ISlide> mISlides = new ArrayList<>();

    public void slideOpen() {
        for (ISlide iSlide : mISlides){
            iSlide.open();
        }
    }

    public void slideClose() {
        for (ISlide iSlide : mISlides){
            iSlide.close();
        }
    }

    public void add(ISlide iSlide) {
        mISlides.add(iSlide);
    }
}
