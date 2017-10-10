package com.example.takunaka.weatherapp.model.dto.api;

import android.support.annotation.NonNull;

import com.example.takunaka.weatherapp.model.dto.forecastDto.Forecast;
import com.example.takunaka.weatherapp.model.dto.weatherDto.CurrentWeather;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {

    //Get запрос в openWeather по погоде сегодня через retrofit
    @NonNull
    @GET("/data/2.5/weather")
    Observable<CurrentWeather> getWeather(@Query("q") String cityName, @Query("units") String units,
                                          @Query("appid") String appid);

    //Get запрос в openWeather по погоде на неделю через retrofit
    @NonNull
    @GET("/data/2.5/forecast/daily")
    Observable<Forecast> getForecast(@Query("q") String cityName, @Query("units") String units,
                                     @Query("appid") String appid);

}
