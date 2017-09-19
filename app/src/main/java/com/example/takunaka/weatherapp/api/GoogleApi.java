package com.example.takunaka.weatherapp.api;

import com.example.takunaka.weatherapp.dto.googleDto.GoogleResponse;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by takunaka on 28.08.17.
 */

public interface GoogleApi {

    @GET("/maps/api/place/photo")
    Observable<ResponseBody> getPhoto(@Query("maxwidth") Integer maxwidth,
                                      @Query("photoreference") String photoreference, @Query("key") String key);

    @GET("/maps/api/place/textsearch/json")
    Observable<GoogleResponse> getID(@Query("query") String cityname, @Query("key") String key);


}
