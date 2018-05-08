package com.app.chenyang.sweather.network;

import com.app.chenyang.sweather.entity.HeWeather;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by chenyang on 2017/3/14.
 */

public interface WeatherService {
    @GET("weather")
    Observable<HeWeather> getWeather(@Query("city") String city_id,
                                     @Query("key") String key);

}
