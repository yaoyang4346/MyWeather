package com.app.chenyang.sweather.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by chenyang on 2017/3/10.
 */

public abstract class LazyFragment extends Fragment {
    private boolean isViewCreated;
    private boolean isUIVisible;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        isViewCreated = true;
        lazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            isUIVisible = true;
            lazyLoad();
        }else{
            isUIVisible = false;
        }
    }

    private void lazyLoad() {
        if(isViewCreated && isUIVisible){
            loadData();
            isViewCreated = true;
            isUIVisible = false;
        }
    }

    public abstract void loadData();

    public abstract void initView(View v);
}
