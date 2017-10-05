package com.example.takunaka.weatherapp.model.dto.api;



import com.example.takunaka.weatherapp.model.dto.googleDto.GoogleResponse;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by takunaka on 28.08.17.
 */

public interface GoogleApi {

    @GET("/maps/api/place/textsearch/json")
    Observable<GoogleResponse> getID(@Query("query") String cityName, @Query("key") String key);


}
