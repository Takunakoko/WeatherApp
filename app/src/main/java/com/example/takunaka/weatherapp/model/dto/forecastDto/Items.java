
package com.example.takunaka.weatherapp.model.dto.forecastDto;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Items {

    @SerializedName("dt")
    @Expose
    private int dt;
    @SerializedName("temp")
    @Expose
    private Temp temp;
    @Nullable
    @SerializedName("weather")
    @Expose
    private List<Weather> weather = null;

    public int getDt() {
        return dt;
    }

    public Temp getTemp() {
        return temp;
    }

    @Nullable
    public List<Weather> getWeather() {
        return weather;
    }

}
