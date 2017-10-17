package com.example.takunaka.weatherapp.model.dto;


import com.example.takunaka.weatherapp.model.dto.forecastDto.Forecast;
import com.example.takunaka.weatherapp.model.dto.googleDto.GoogleResponse;
import com.example.takunaka.weatherapp.model.dto.weatherDto.CurrentWeather;



public class Data {
    private CurrentWeather currentWeather;
    private Forecast forecast;
    private GoogleResponse googleResponse;

    public Data(CurrentWeather currentWeather, Forecast forecast, GoogleResponse googleResponse) {
        this.currentWeather = currentWeather;
        this.forecast = forecast;
        this.googleResponse = googleResponse;
    }

    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public GoogleResponse getGoogleResponse() {
        return googleResponse;
    }

}
