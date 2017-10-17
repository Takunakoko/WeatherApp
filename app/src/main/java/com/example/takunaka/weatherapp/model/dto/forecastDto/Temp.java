
package com.example.takunaka.weatherapp.model.dto.forecastDto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Temp {

    @SerializedName("max")
    @Expose
    private double max;

    public double getMax() {
        return max;
    }


}
