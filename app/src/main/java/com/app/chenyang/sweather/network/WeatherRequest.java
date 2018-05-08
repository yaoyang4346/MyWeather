package com.app.chenyang.sweather.network;

import com.app.chenyang.sweather.entity.HeWeather;
import com.app.chenyang.sweather.global.MyConst;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by chenyang on 2017/3/14.
 */

public class WeatherRequest {
    public static final String BASE_URL = "https://free-api.heweather.com/v5/";
    private static final int CONNECT_TIMEOUT = 10;
    private static final int WRITE_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 20;
    private Retrofit retrofit;
    private WeatherService service;

    private WeatherRequest(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT,TimeUnit.SECONDS);
        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        service = retrofit.create(WeatherService.class);
    }

    private static class SingletonFactory{
        private static final WeatherRequest INSTANCE = new WeatherRequest();
    }

    public static WeatherRequest getInstance(){
        return SingletonFactory.INSTANCE;
    }

    public Subscription getWeather(Action1<HeWeather> a1, Action1<Throwable> a2, String city){
        return service.getWeather(city, MyConst.KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(a1,a2);
    }

    public Subscription getWeatherNowThread(Action1<HeWeather> a1, Action1<Throwable> a2, String city){
        return service.getWeather(city, MyConst.KEY)
                .subscribe(a1,a2);
    }
}
