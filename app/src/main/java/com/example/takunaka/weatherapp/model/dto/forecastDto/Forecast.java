
package com.example.takunaka.weatherapp.model.dto.forecastDto;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Forecast {

    @Nullable
    @SerializedName("list")
    @Expose
    private List<Items> list = null;

    @Nullable
    public List<Items> getList() {
        return list;
    }


}
