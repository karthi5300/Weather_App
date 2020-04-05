package com.karthick.weatherapp;

import com.karthick.weatherapp.data.ForecastData;
import com.karthick.weatherapp.data.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapApi {

    //CURRENT WEATHER
    @GET("data/2.5/weather?")
    Call<WeatherData> getWeather(@Query("q") String city, @Query("appid") String appId);


    //WEATHER FORECAST
    @GET("data/2.5/forecast/daily?")
    Call<ForecastData> getForecast(@Query("q") String city, @Query("cnt") int days, @Query("appid") String appId);

}




