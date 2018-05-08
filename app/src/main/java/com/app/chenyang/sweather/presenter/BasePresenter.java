package com.app.chenyang.sweather.presenter;

import com.app.chenyang.sweather.utils.LogUtils;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import rx.Subscription;

/**
 * Created by chenyang on 2017/3/29.
 */

public abstract class BasePresenter<T> {
    protected Reference<T> mViewReference;
    public ArrayList<Subscription> rxList;

    public void attachView(T view){
        rxList = new ArrayList<>();
        mViewReference = new WeakReference<T>(view);
    }

    protected T getView(){
        return mViewReference.get();
    }

    public boolean isViewAttached(){
        return mViewReference != null && mViewReference.get() != null;
    }

    public void detachView(){
        for (Subscription subscription : rxList){
            if(subscription != null && !subscription.isUnsubscribed()){
                subscription.unsubscribe();
                LogUtils.d("unsubscribe");
            }
        }
        rxList.clear();
        if(mViewReference != null){
            mViewReference.clear();
            mViewReference = null;
        }
    }
}