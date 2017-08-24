package com.example.takunaka.weatherapp.api;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by takunaka on 21.08.17.
 */

public class WeatherClient {

    private static final String url = "http://api.openweathermap.org";

    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(url) //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static WeatherApi getApi() {
        return getRetrofitInstance().create(WeatherApi.class);
    }
}