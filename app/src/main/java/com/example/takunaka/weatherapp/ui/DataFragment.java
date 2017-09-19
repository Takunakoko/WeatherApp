package com.example.takunaka.weatherapp.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.takunaka.weatherapp.R;
import com.example.takunaka.weatherapp.api.WeatherApi;
import com.example.takunaka.weatherapp.api.WeatherClient;
import com.example.takunaka.weatherapp.dto.forecastDto.Forecast;
import com.example.takunaka.weatherapp.dto.forecastDto.Items;
import com.example.takunaka.weatherapp.dto.weatherDto.CurrentWeather;
import com.example.takunaka.weatherapp.dto.weatherDto.Main;
import com.example.takunaka.weatherapp.dto.weatherDto.Weather;
import com.example.takunaka.weatherapp.dto.weatherDto.Wind;
import com.example.takunaka.weatherapp.util.Cfg;

import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class DataFragment extends Fragment {
    private View rootView;
    private Cfg cfg = Cfg.getInstance();
    private RelativeLayout rl;

    private ImageView image;
    private TextView temp;
    private TextView minTemp;
    private TextView maxTemp;
    private TextView pressure;
    private TextView wind;
    private TextView day1;
    private TextView day2;
    private TextView day3;
    private TextView day4;
    private TextView day5;
    private TextView day6;
    private TextView day7;

    private String city;
    private WeatherApi api;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_data, container, false);
        initViews();
        api = WeatherClient.getApi();

        if(savedInstanceState!=null){
            updateData(city);
        }
        return rootView;
    }


    public void setMain(Main main){
        temp.setText(String.valueOf((int)main.getTemp() + "°C"));
        pressure.setText(String.valueOf((int)(main.getPressure() * 0.75006375541921) + "mm"));
        maxTemp.setText(String.valueOf((int)main.getTempMax() + "°C"));
        minTemp.setText(String.valueOf((int)main.getTempMin() + "°C"));
    }

    public void setWind(Wind w){
        wind.setText(String.valueOf((int)w.getSpeed() + "m/s"));
    }

    public void setPic(List<Weather> list){
        image.setBackgroundResource(returnImg(list.get(0).getMain()));

    }

    public void callForecast(List<Items> items){
        forecastDayTemp(day1, items.get(0).getDt(), items.get(0).getTemp().getMax()
                , items.get(0).getWeather().get(0).getMain());
        forecastDayTemp(day2, items.get(1).getDt(), items.get(1).getTemp().getMax()
                , items.get(1).getWeather().get(0).getMain());
        forecastDayTemp(day3, items.get(2).getDt(), items.get(2).getTemp().getMax()
                , items.get(2).getWeather().get(0).getMain());
        forecastDayTemp(day4, items.get(3).getDt(), items.get(3).getTemp().getMax()
                , items.get(3).getWeather().get(0).getMain());
        forecastDayTemp(day5, items.get(4).getDt(), items.get(4).getTemp().getMax()
                , items.get(4).getWeather().get(0).getMain());
        forecastDayTemp(day6, items.get(5).getDt(), items.get(5).getTemp().getMax()
                , items.get(5).getWeather().get(0).getMain());
        forecastDayTemp(day7, items.get(6).getDt(), items.get(6).getTemp().getMax()
                , items.get(6).getWeather().get(0).getMain());
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
        rl = (RelativeLayout) rootView.findViewById(R.id.loading_frame);
    }

    public void updateData(String city){
        this.city = city;
        api.getForecast(city, cfg.getUnits(), cfg.getForecastApiKey())
                .map(Forecast::getList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::showLoad)
                .doAfterTerminate(this::hideLoad)
                .subscribe(this::callForecast);

        api.getWeather(city, cfg.getUnits(), cfg.getForecastApiKey())
                .map(CurrentWeather::getMain)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::showLoad)
                .doAfterTerminate(this::hideLoad)
                .subscribe(this::setMain);

        api.getWeather(city, cfg.getUnits(), cfg.getForecastApiKey())
                .map(CurrentWeather::getWind)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::showLoad)
                .doAfterTerminate(this::hideLoad)
                .subscribe(this::setWind);

        api.getWeather(city, cfg.getUnits(), cfg.getForecastApiKey())
                .map(CurrentWeather::getWeather)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::showLoad)
                .doAfterTerminate(this::hideLoad)
                .subscribe(this::setPic);
    }

    public void forecastDayTemp(TextView tw, int dateTime, double temp, String desc){
        tw.setCompoundDrawablesWithIntrinsicBounds(0, returnImg(desc), 0, 0);
        Date date = new Date(dateTime*1000);
        String[] dateArray = date.toString().split(" ");
        tw.setText(String.valueOf((int) temp + "°С" + "\n" + dateArray[0]));
    }

    public void showLoad(Disposable disposable){
        rl.setVisibility(View.VISIBLE);
    }

    public void hideLoad(){
        rl.setVisibility(View.GONE);
    }

}
