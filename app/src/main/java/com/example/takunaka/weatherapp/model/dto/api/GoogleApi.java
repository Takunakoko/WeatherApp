package com.example.takunaka.weatherapp.model.dto.api;



import android.support.annotation.NonNull;

import com.example.takunaka.weatherapp.model.dto.googleDto.GoogleResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface GoogleApi {

    //Get запрос в google через retrofit
    @NonNull
    @GET("/maps/api/place/textsearch/json")
    Observable<GoogleResponse> getID(@Query("query") String cityName, @Query("key") String key);


}
