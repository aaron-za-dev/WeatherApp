package com.aaronzadev.weatherapp.service;

import com.aaronzadev.weatherapp.pojo.MyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("weather")
    Call<MyResponse> response(@Query("lat") String lat, @Query("lon") String lon,
                              @Query("lang") String lang, @Query("units") String unit, @Query("appid") String appid);


}
