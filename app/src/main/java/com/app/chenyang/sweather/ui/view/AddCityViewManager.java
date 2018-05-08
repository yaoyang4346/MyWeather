package com.app.chenyang.sweather.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.MapView;
import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.utils.LogUtils;

/**
 * Created by chenyang on 2017/3/31.
 */

public class AddCityViewManager extends FrameLayout {
    public static final int TEXT_MODE = 0;
    public static final int MAP_MODE = 1;
    private View idleView;
    private View loadingView;
    private View errorView;
    private View searchSuccessView;
    private View mapView;
    private TextView tv;
    private ImageView iv;
    private MapView mMapView;
    private AddCityState currentState = AddCityState.STATE_IDLE;
    private int currentMode = TEXT_MODE;

    public AddCityViewManager(@NonNull Context context) {
        this(context,null);
    }

    public AddCityViewManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        idleView = View.inflate(context, R.layout.add_city_idle,null);
        loadingView = View.inflate(context, R.layout.add_city_loading,null);
        errorView = View.inflate(context, R.layout.add_city_error_view,null);
        searchSuccessView = View.inflate(context, R.layout.add_city_list,null);
        mapView = View.inflate(context,R.layout.add_city_map,null);
        mMapView = (MapView) mapView.findViewById(R.id.map);
    }

    public void switchMode(int mode){
        if(currentMode == MAP_MODE && mode == TEXT_MODE){
            LogUtils.d("map onpause");
            mMapView.onPause();
        }

        currentMode = mode;
        setState(AddCityState.STATE_IDLE);
    }

    public void setState(AddCityState state){
        if(currentMode == MAP_MODE && currentState != AddCityState.STATE_IDLE){
            LogUtils.d("map onpause");
            mMapView.onPause();
        }
        removeAllViews();
        currentState = state;
        switch (state){
            case STATE_IDLE:
                if(currentMode == TEXT_MODE){
                    addView(idleView);
                }else{
                    LogUtils.d("map onresume");
                    mMapView.onResume();
                    addView(mapView);
                }
                break;
            case STATE_NULL:
                getAndSetView(R.string.search_city_null,R.drawable.ic_null_search);
                addView(errorView);
                break;
            case STATE_LOADING:
                addView(loadingView);
                break;
            case STATE_SEARCH_SUCCESS:
                addView(searchSuccessView);
                break;
            case STATE_NET_ERROR:
                getAndSetView(R.string.error_net,R.drawable.ic_net_error);
                addView(errorView);
                break;
            case STATE_SERVE_ERROR:
                getAndSetView(R.string.error_serve,R.drawable.ic_serve_error);
                addView(errorView);
                break;
            default:
                break;
        }
    }

    private void getAndSetView(int textRes, int imgRes){
        if(tv == null){
            tv = (TextView) errorView.findViewById(R.id.tv_error);
        }
        tv.setText(textRes);
        if(iv == null){
            iv = (ImageView) errorView.findViewById(R.id.iv_error);
        }
        iv.setImageResource(imgRes);
    }

    public enum AddCityState{
        STATE_IDLE,STATE_NULL,STATE_LOADING,STATE_SEARCH_SUCCESS
        ,STATE_SERVE_ERROR,STATE_NET_ERROR
    }

    public MapView getMap(){
        return mMapView;
    }

    public int getCurrentMode(){
        return currentMode;
    }

    public AddCityState getCurrentState(){
        return currentState;
    }
}
