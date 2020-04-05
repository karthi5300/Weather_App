package com.karthick.weatherapp;

import com.karthick.weatherapp.data.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapApi {

    @GET("data/2.5/weather?")
    Call<WeatherData> getWeather(@Query("q") String city, @Query("appid") String appId);
}

