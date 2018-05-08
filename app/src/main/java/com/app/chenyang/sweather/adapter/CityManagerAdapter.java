package com.app.chenyang.sweather.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.adapter.viewholder.CityManagerViewHolder;
import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.helper.RecycleSlideHelp.SlideHelper;
import com.app.chenyang.sweather.helper.RecycleViewHelp.OnStartDragListener;
import com.app.chenyang.sweather.utils.BaseUtils;
import com.app.chenyang.sweather.utils.LogUtils;
import com.app.chenyang.sweather.utils.PrefUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by chenyang on 2017/4/10.
 */

public class CityManagerAdapter extends RecyclerView.Adapter<CityManagerViewHolder> {
    private static final int NORMAL_MODE = 0;
    private static final int EDIT_MODE = 1;
    private OnStartDragListener mDragStartListener;
    private ArrayList<WeatherDataInfo> allCity;
    private boolean positionChange = false;
    private ArrayList<WeatherDataInfo> checkList;
    private SlideHelper mSlideHelper = new SlideHelper();
    private int currentMode = NORMAL_MODE;
    private Drawable locationIcon;

    public CityManagerAdapter(ArrayList<WeatherDataInfo> allCity){
        this.allCity = allCity;
        checkList = new ArrayList<>();
        locationIcon = BaseUtils.getContext().getResources().getDrawable(R.drawable.ic_location_on_black);
        locationIcon.setTint(Color.BLACK);
        locationIcon.setBounds(0,0, locationIcon.getMinimumWidth(), locationIcon.getMinimumHeight());
    }

    public void setOnDragStartListener(OnStartDragListener mDragStartListener){
        this.mDragStartListener = mDragStartListener;
    }

    @Override
    public CityManagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CityManagerViewHolder viewHolder =  new CityManagerViewHolder(LayoutInflater.from(BaseUtils.getContext()).inflate(R.layout.city_manager_item,parent,false));
        mSlideHelper.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CityManagerViewHolder holder, int position) {
        holder.onBindSlide();
        WeatherDataInfo weatherDataInfo = allCity.get(position);
        if (weatherDataInfo!=null){
            holder.tvCity.setText(weatherDataInfo.getName());
            holder.tvCity.setCompoundDrawables(null,null,weatherDataInfo.isGps()?locationIcon:null,null);
            holder.tvProvince.setText(weatherDataInfo.getProvince()+","+BaseUtils.getString(R.string.china));
            holder.tvTime.setText(weatherDataInfo.getServeTime());
            holder.tvTemp.setText(weatherDataInfo.getTmp());
            String max_min = weatherDataInfo.getMaxTmp().get(0)+BaseUtils.getString(R.string.temperature).substring(0,1)
                    +"/"+weatherDataInfo.getMinTmp().get(0)+BaseUtils.getString(R.string.temperature).substring(0,1);
            holder.tvMaxMin.setText(max_min);
            holder.ivIcon.setImageResource(weatherDataInfo.getIcon());
            holder.cbItem.setEnabled(!weatherDataInfo.isGps());
            holder.cbItem.setChecked(checkList.contains(weatherDataInfo),false);
        }
        holder.ivSort.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) ==
                        MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return allCity.size();
    }

    public void move(int fromPosition,int toPosition){
        positionChange = true;
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(allCity, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(allCity, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public void setMode(int mode){
        currentMode = mode;
        if(mode == EDIT_MODE){
            mSlideHelper.slideOpen();
            return;
        }
        mSlideHelper.slideClose();

    }

    public ArrayList<WeatherDataInfo> getAllCity(){
        return allCity;
    }

    public void setAllCity(ArrayList<WeatherDataInfo> allCity){
        if (allCity == null){
            this.allCity = new ArrayList<>();
            return;
        }
        this.allCity = allCity;
    }

    public boolean isPositionChange(){
        return positionChange;
    }

    public void updataChangeState(boolean b){
        positionChange = b;
    }

    public void adjustCheck(WeatherDataInfo id){
        if(id.isGps()){
            return;
        }
        if(checkList.contains(id)){
            checkList.remove(id);
            return;
        }
        checkList.add(id);
    }

    public void addAllToCheckList(){
        checkList.clear();
        for (WeatherDataInfo weatherDataInfo : allCity){
            if (!weatherDataInfo.isGps()){
                checkList.add(weatherDataInfo);
            }
        }

    }

    public ArrayList<WeatherDataInfo> getCheckList(){
        return checkList;
    }

    public void clearCheckList(){
        checkList.clear();
    }

    public void deleteCityFromList(){
        allCity.removeAll(checkList);
    }


}