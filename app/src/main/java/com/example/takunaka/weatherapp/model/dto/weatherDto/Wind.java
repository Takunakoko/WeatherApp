
package com.example.takunaka.weatherapp.model.dto.weatherDto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wind {

    @SerializedName("speed")
    @Expose
    private double speed;

    public double getSpeed() {
        return speed;
    }

}
