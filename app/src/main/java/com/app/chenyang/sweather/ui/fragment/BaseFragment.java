package com.app.chenyang.sweather.ui.fragment;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.adapter.LivingDetailAdapter;
import com.app.chenyang.sweather.db.WeatherDao;
import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.ui.widget.WeatherChartView;
import com.app.chenyang.sweather.utils.BaseUtils;
import com.app.chenyang.sweather.utils.LogUtils;

import org.litepal.crud.DataSupport;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenyang on 2017/2/15.
 */

public class BaseFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener{
    @BindView(R.id.app_bar) AppBarLayout appBar;
    @BindView(R.id.page_back) ImageView ivPageBack;
    @BindView(R.id.page_forward) ImageView ivPageForward;
    @BindView(R.id.tv_weather_aqi) TextView tvAQI;
    @BindView(R.id.tv_weather) TextView tvWeather;
    @BindView(R.id.tv_weather_city) TextView tvCity;
    @BindView(R.id.tv_weather_time) TextView tvTime;
    @BindView(R.id.ll_weather) LinearLayout llWeather;
    @BindView(R.id.gv_living_details) GridView gvLivingDetail;
    @BindView(R.id.daily_chart) WeatherChartView chartView;
    @BindView(R.id.iv_weather_icon) ImageView ivIcon;
    @BindView(R.id.tv_weather_temp) TextView tvTemp;
    @BindView(R.id.ns) NestedScrollView nestedScrollView;

    private static final String CITY_POSITION = "position";
    private static final int ALPHA_DISTANCE = 60;
    private static final float ZOOM_RATIO = 0.2f;
    private int position;
    private RelativeLayout.LayoutParams ivPageBackParams;
    private RelativeLayout.LayoutParams ivPageForwardParams;
    private FrameLayout.LayoutParams tvCityParams;
    private FrameLayout.LayoutParams tvTimeParams;
    private FrameLayout.LayoutParams llWeatherParams;
    private int ivPageBackTopMargin;
    private int ivPageForwardTopMargin;
    private int tvCityTopMargin;
    private int tvTimeTopMargin;
    private int llWeatherTopMargin;
    private View view;
    private Drawable locationIcon;
    private boolean isCreate;

    public static BaseFragment newInstance(int position){
        BaseFragment fragment = new BaseFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CITY_POSITION,position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(CITY_POSITION,-2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.layout_weather,null);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isCreate = true;
        locationIcon = getResources().getDrawable(R.drawable.ic_location_on_black);
        locationIcon.setTint(Color.WHITE);
        locationIcon.setBounds(0,0, locationIcon.getMinimumWidth(), locationIcon.getMinimumHeight());
        setScrollRule();
        displayData(WeatherDao.getDataByPosition(position));
    }

    @Override
    public void onResume() {
        super.onResume();
        setArrow();
    }

    public void refreshData(){
        LogUtils.d("refresh page:" + position);
        displayData(WeatherDao.getDataByPosition(position));
    }

    public int getCurrentPosition(){
        return position;
    }

    private void displayData(WeatherDataInfo dataInfo) {
        LogUtils.d("display page:" + position);
        if(dataInfo != null){
            tvCity.setText(dataInfo.getName());
            tvCity.setCompoundDrawables(dataInfo.isGps()? locationIcon :null,null,null,null);
            tvTime.setText(dataInfo.getServeTime());
            ivIcon.setImageResource(dataInfo.getIcon());
            tvTemp.setText(dataInfo.getTmp());
            tvAQI.setText(dataInfo.getAqi());
            tvWeather.setText(dataInfo.getWeather());
            chartView.setTemperatureAndIcon(dataInfo.getMaxTmp(),dataInfo.getMinTmp(),dataInfo.getDailyIcon());
            if (getUserVisibleHint()){
                chartView.startAnimation();
            }
            LivingDetailAdapter adapter = new LivingDetailAdapter(dataInfo.getLivingIndex());
            gvLivingDetail.setAdapter(adapter);
        }else{
            tvCity.setText(getString(R.string.error_data));
            tvTime.setText(getString(R.string.clear_app_data));
            ivIcon.setImageResource(R.mipmap.s999);
            tvTemp.setText(getString(R.string.unknow_data));
            tvAQI.setText(getString(R.string.unknow_data));
            tvWeather.setText(getString(R.string.unknow_data));
            chartView.setTemperatureAndIcon(null,null,null);
        }
    }

    private void setArrow(){
        int pageCount = DataSupport.count(WeatherDataInfo.class);

        if(position == 0){
            ivPageBack.setVisibility(View.INVISIBLE);
            if(position == pageCount - 1){
                ivPageForward.setVisibility(View.INVISIBLE);
            }else{
                ivPageForward.setVisibility(View.VISIBLE);
            }
        }else if(position == pageCount - 1){
            ivPageForward.setVisibility(View.INVISIBLE);
        }else{
            ivPageBack.setVisibility(View.VISIBLE);
            ivPageForward.setVisibility(View.VISIBLE);
        }
    }

    private void setScrollRule() {
        ivPageBackParams = (RelativeLayout.LayoutParams) ivPageBack.getLayoutParams();
        ivPageForwardParams = (RelativeLayout.LayoutParams) ivPageForward.getLayoutParams();
        ivPageBackTopMargin = ivPageBackParams.topMargin;
        ivPageForwardTopMargin = ivPageForwardParams.topMargin;

        tvCityParams = (FrameLayout.LayoutParams) tvCity.getLayoutParams();
        tvTimeParams = (FrameLayout.LayoutParams) tvTime.getLayoutParams();
        tvCityTopMargin = tvCityParams.topMargin;
        tvTimeTopMargin = tvTimeParams.topMargin;

        llWeatherParams = (FrameLayout.LayoutParams) llWeather.getLayoutParams();
        llWeatherTopMargin = llWeatherParams.topMargin;

        tvCity.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isCreate){
            if(isVisibleToUser){
                chartView.startAnimation();
            }else{
                chartView.cleanAnimation();
            }
        }
    }

    @Override
    public void onGlobalLayout() {
        tvCity.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        nestedScrollView.fullScroll(View.FOCUS_UP);
        int ivArrowDistance = ivPageBack.getRight();
        final int scrollRange = appBar.getTotalScrollRange();
        final double tvCityLeftTranslationDistance = tvCity.getLeft() + (tvCity.getWidth() * ZOOM_RATIO * 0.5) - ivArrowDistance;
        final double tvTimeLeftTranslationDistance = tvTime.getLeft() + (tvTime.getWidth() * ZOOM_RATIO * 0.5) - ivArrowDistance;
        final double llWeatherRightTranslationDistance = (BaseUtils.SCREEN_WIDTH - llWeather.getRight()) + (llWeather.getWidth() * ZOOM_RATIO * 0.5) - ivArrowDistance;
        final double llWeatherTopTranslationDistance = ivPageForwardTopMargin + scrollRange/2 + ivPageBack.getHeight() * 0.5 - llWeather.getHeight() * 0.5 - llWeatherTopMargin;

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollDistance = Math.abs(verticalOffset);
                float scrollPercentage = (float) scrollDistance/scrollRange;

                ivPageBackParams.topMargin = ivPageBackTopMargin + scrollDistance/2;
                ivPageForwardParams.topMargin = ivPageForwardTopMargin + scrollDistance/2;
                ivPageBack.setLayoutParams(ivPageBackParams);
                ivPageForward.setLayoutParams(ivPageForwardParams);

                if(scrollDistance > ALPHA_DISTANCE){
                    tvAQI.setAlpha(0);
                    tvWeather.setAlpha(0);
                }else{
                    tvAQI.setAlpha(1-scrollDistance/(float)ALPHA_DISTANCE);
                    tvWeather.setAlpha(1-scrollDistance/(float)ALPHA_DISTANCE);
                }

                tvCityParams.topMargin = tvCityTopMargin + scrollDistance;
                tvCityParams.leftMargin = -(int) (tvCityLeftTranslationDistance * scrollPercentage);

                tvTimeParams.topMargin = tvTimeTopMargin + scrollDistance;
                tvTimeParams.leftMargin = -(int) (tvTimeLeftTranslationDistance * scrollPercentage);

                tvCity.setLayoutParams(tvCityParams);
                tvCity.setScaleX(1 - ZOOM_RATIO * scrollPercentage);
                tvCity.setScaleY(1 - ZOOM_RATIO * scrollPercentage);

                tvTime.setLayoutParams(tvTimeParams);
                tvTime.setScaleX(1 -ZOOM_RATIO * scrollPercentage);
                tvTime.setScaleY(1 -ZOOM_RATIO * scrollPercentage);

                llWeatherParams.topMargin = (int) (llWeatherTopTranslationDistance * scrollPercentage) + llWeatherTopMargin;
                llWeatherParams.rightMargin = -(int) (llWeatherRightTranslationDistance * scrollPercentage);
                llWeather.setScaleX(1 - ZOOM_RATIO * scrollPercentage);
                llWeather.setScaleY(1 - ZOOM_RATIO * scrollPercentage);
            }
        });

    }
}
