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
    private String units = "metric";
    private String googlePlacesApiKey = "AIzaSyC0CZDjctE8xKKa-F_MiBpSsONDuSYVUDM";


    public String getForecastApiKey(){
        return forecastApiKey;
    }

    public String getUnits() {
        return units;
    }

    public String getGooglePlacesApiKey() {
        return googlePlacesApiKey;
    }
}
