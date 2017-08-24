package com.example.takunaka.weatherapp.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.takunaka.weatherapp.dto.forecastDto.Items;
import com.example.takunaka.weatherapp.util.Cfg;
import com.example.takunaka.weatherapp.R;
import com.example.takunaka.weatherapp.api.WeatherApi;
import com.example.takunaka.weatherapp.api.WeatherClient;
import com.example.takunaka.weatherapp.dto.forecastDto.Forecast;

import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class DataFragment extends Fragment {
    private View rootView;
    private Cfg cfg = Cfg.getInstance();

    private ImageView image;
    private TextView temp;
    private TextView minTemp;
    private TextView maxTemp;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView day1;
    private TextView day2;
    private TextView day3;
    private TextView day4;
    private TextView day5;
    private TextView day6;
    private TextView day7;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_data, container, false);
        initViews();


        WeatherApi fApi = WeatherClient.getApi();

        fApi.getForecast("Moscow", cfg.getUnits(), cfg.getForecastApiKey())
                .map(Forecast::getList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::callForecast);

        return rootView;
    }

    public void callWeather(List<Items> items){

    }

    public void callForecast(List<Items> items){

        items.get(0).
    }

    public int returnImg(String description){
        if(description.equals("Drizzle") || description.equals("Rain")){
            return R.drawable.ic_rain;
        } else if (description.equals("Thunderstorm")){
            return R.drawable.ic_storm;
        } else if (description.equals("Snow")){
            return R.drawable.ic_snowflake;
        }else if (description.equals("Clear")){
            return R.drawable.ic_sunny;
        } else if (description.equals("Clouds")){
            return R.drawable.ic_clouds;
        } else {
            return R.drawable.ic_sunny;
        }
    }

    public void initViews(){
        day1 = (TextView) rootView.findViewById(R.id.day1);
        day2 = (TextView) rootView.findViewById(R.id.day2);
        day3 = (TextView) rootView.findViewById(R.id.day3);
        day4 = (TextView) rootView.findViewById(R.id.day4);
        day5 = (TextView) rootView.findViewById(R.id.day5);
        day6 = (TextView) rootView.findViewById(R.id.day6);
        day7 = (TextView) rootView.findViewById(R.id.day7);
        image = (ImageView) rootView.findViewById(R.id.current_weather_img);
        temp = (TextView) rootView.findViewById(R.id.current_temp);
        minTemp = (TextView) rootView.findViewById(R.id.min_temp);
        maxTemp = (TextView) rootView.findViewById(R.id.max_temp);
        pressure = (TextView) rootView.findViewById(R.id.pressure);
        wind = (TextView) rootView.findViewById(R.id.wind);
        sunrise = (TextView) rootView.findViewById(R.id.sunrise);
        sunset = (TextView) rootView.findViewById(R.id.sunset);
    }

    public void updateData(String city){
        callWeather(city);
        callForecast(city);
    }

    public String formattedDate (Long dateNum){
        Date date = new Date(dateNum*1000);
        String[] dateArray = date.toString().split(" ");
        return dateArray[3];
    }

    public String forecastDayTemp(Long dateTime, double temp){
        Date date = new Date(dateTime*1000);
        String[] dateArray = date.toString().split(" ");
        return new String((int) temp + "°С" + "\n" + dateArray[0]);
    }


}
