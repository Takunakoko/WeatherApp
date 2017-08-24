package com.example.takunaka.weatherapp.util;

/**
 * Created by takunaka on 21.08.17.
 */

public class Cfg {

    private static Cfg instance;

    private Cfg(){

    }

    public static Cfg getInstance(){
        if(instance == null){
            instance = new Cfg();
        }return instance;
    }

    private String forecastApiKey = "f6f1b3b3859fe2d221fb485da1b99798";
    private String apiKey = "e0de28a8cb0fee81c2e98e66f2480232";
    private String units = "metric";

    public String getApiKey() {
        return apiKey;
    }

    public String getForecastApiKey(){
        return forecastApiKey;
    }

    public String getUnits() {
        return units;
    }
}
