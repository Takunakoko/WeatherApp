
package com.example.takunaka.weatherapp.model.dto.weatherDto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Weather {

    @SerializedName("main")
    @Expose
    private String main;


    public String getMain() {
        return main;
    }

}
