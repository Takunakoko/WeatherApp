package com.example.takunaka.weatherapp.util;


public abstract class Config {

    //ключ OpenWeatherApi
    public static final String FORECAST_API_KEY = "f6f1b3b3859fe2d221fb485da1b99798";
    //стартовая ссылка WeatherApi
    public static final String WEATHER_URL = "http://api.openweathermap.org";
    //метод измерения
    public static final String UNITS = "metric";
    //Ключ GoogleApi
    public static final String GOOGLE_PLACES_API_KEY = "AIzaSyA8y21BP_dFeb2pVFoLx1629mwGbIA7wFI";
    //стартовая ссылка GoogleApi
    public static final String GOOGLE_URL = "https://maps.googleapis.com";
    //продолжение ссылки для получения изображения
    public static final String GOOGLE_PIC = "/maps/api/place/photo?";
    //Максимальная ширина изображения
    public static final String MAXWIDTH = "maxwidth=5000";

}
