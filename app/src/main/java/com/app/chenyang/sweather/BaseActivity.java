package com.app.chenyang.sweather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.app.chenyang.sweather.presenter.BasePresenter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import rx.Subscription;

/**
 * Created by chenyang on 2017/3/31.
 */

public abstract class BaseActivity<V , T extends BasePresenter<V>> extends AppCompatActivity {
    protected T mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        mPresenter.attachView((V) this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    public abstract  T createPresenter();
}
