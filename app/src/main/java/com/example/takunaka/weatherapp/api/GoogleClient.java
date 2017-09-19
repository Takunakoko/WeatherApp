package com.example.takunaka.weatherapp.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by takunaka on 28.08.17.
 */

public class GoogleClient {
    private static final String url = "https://maps.googleapis.com";

    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(url) //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static GoogleApi getApi() {
        return getRetrofitInstance().create(GoogleApi.class);
    }
}
