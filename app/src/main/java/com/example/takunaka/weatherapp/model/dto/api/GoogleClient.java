package com.example.takunaka.weatherapp.model.dto.api;

import com.example.takunaka.weatherapp.util.Config;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleClient {

    private static Retrofit getRetrofitInstance() {
        //возврат клента ретрофита для запроса в гугл
        return new Retrofit.Builder()
                .baseUrl(Config.GOOGLE_URL) //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static GoogleApi getApi() {
        return getRetrofitInstance().create(GoogleApi.class);
    }
}
