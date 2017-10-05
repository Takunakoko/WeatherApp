package com.example.takunaka.weatherapp.view;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takunaka.weatherapp.R;
import com.example.takunaka.weatherapp.model.dto.api.WeatherApi;
import com.example.takunaka.weatherapp.model.dto.api.WeatherClient;
import com.example.takunaka.weatherapp.model.dto.forecastDto.Forecast;
import com.example.takunaka.weatherapp.model.dto.forecastDto.Items;
import com.example.takunaka.weatherapp.model.dto.weatherDto.CurrentWeather;
import com.example.takunaka.weatherapp.model.dto.weatherDto.Main;
import com.example.takunaka.weatherapp.model.dto.weatherDto.Weather;
import com.example.takunaka.weatherapp.model.dto.weatherDto.Wind;
import com.example.takunaka.weatherapp.util.Config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class DataPresenter {

    private DataFragment dataFragment;
    private WeatherApi api;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);

    public DataPresenter(@NonNull DataFragment dataFragment) {
        this.dataFragment = dataFragment;
        api = WeatherClient.getApi();
    }

    public void loadData(String city){
        api.getForecast(city, Config.UNITS, Config.FORECAST_API_KEY)
                .map(Forecast::getList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::showLoad)
                .doAfterTerminate(this::hideLoad)
                .subscribe(this::callForecast, throwable -> showError(throwable));

        api.getWeather(city, Config.UNITS, Config.FORECAST_API_KEY)
                .map(CurrentWeather::getMain)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::showLoad)
                .doAfterTerminate(this::hideLoad)
                .subscribe(this::setMain, throwable -> showError(throwable));

        api.getWeather(city, Config.UNITS, Config.FORECAST_API_KEY)
                .map(CurrentWeather::getWind)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::showLoad)
                .doAfterTerminate(this::hideLoad)
                .subscribe(this::setWind, throwable -> showError(throwable));

        api.getWeather(city, Config.UNITS, Config.FORECAST_API_KEY)
                .map(CurrentWeather::getWeather)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::showLoad)
                .doAfterTerminate(this::hideLoad)
                .subscribe(this::setPic, throwable -> showError(throwable));
    }

    private void forecastDayTemp(TextView day, TextView dayTemp, ImageView dayIcon, int dateTime, double temp, String desc){
        dayIcon.setBackgroundResource(returnImg(desc));
        day.setText(sdf.format(new Date(dateTime * 1000L)));
        dayTemp.setText(String.valueOf((int) temp + "°С"));
    }


    private void setMain(Main main){
        dataFragment.temp.setText(String.valueOf((int)main.getTemp() + "°C"));
        dataFragment.pressure.setText(String.valueOf((int)(main.getPressure() * 0.75006375541921) + "mm"));
        dataFragment.maxTemp.setText(String.valueOf((int)main.getTempMax() + "°C"));
        dataFragment.minTemp.setText(String.valueOf((int)main.getTempMin() + "°C"));
    }

    private void setWind(Wind w){
        dataFragment.wind.setText(String.valueOf((int)w.getSpeed() + "m/s"));
    }

    private void setPic(List<Weather> list){
        dataFragment.image.setBackgroundResource(returnImg(list.get(0).getMain()));

    }

    private void callForecast(List<Items> items){
        forecastDayTemp(dataFragment.day1, dataFragment.day1_temp, dataFragment.day1_img,
                items.get(0).getDt(), items.get(0).getTemp().getMax(), items.get(0).getWeather().get(0).getMain());
        forecastDayTemp(dataFragment.day2, dataFragment.day2_temp, dataFragment.day2_img,
                items.get(1).getDt(), items.get(1).getTemp().getMax(), items.get(1).getWeather().get(0).getMain());
        forecastDayTemp(dataFragment.day3, dataFragment.day3_temp, dataFragment.day3_img,
                items.get(2).getDt(), items.get(2).getTemp().getMax(), items.get(2).getWeather().get(0).getMain());
        forecastDayTemp(dataFragment.day4, dataFragment.day4_temp, dataFragment.day4_img,
                items.get(3).getDt(), items.get(3).getTemp().getMax(), items.get(3).getWeather().get(0).getMain());
        forecastDayTemp(dataFragment.day5, dataFragment.day5_temp, dataFragment.day5_img,
                items.get(4).getDt(), items.get(4).getTemp().getMax(), items.get(4).getWeather().get(0).getMain());
        forecastDayTemp(dataFragment.day6, dataFragment.day6_temp, dataFragment.day6_img,
                items.get(5).getDt(), items.get(5).getTemp().getMax(), items.get(5).getWeather().get(0).getMain());
        forecastDayTemp(dataFragment.day7, dataFragment.day7_temp, dataFragment.day7_img,
                items.get(6).getDt(), items.get(6).getTemp().getMax(), items.get(6).getWeather().get(0).getMain());
    }

    private int returnImg(String description){
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

    private void showLoad(Disposable disposable){
        //rl.setVisibility(View.VISIBLE);
    }

    private void hideLoad(){
        //rl.setVisibility(View.GONE);
    }

    private void showError(Throwable t) {
        Log.e("Error : ", t.getMessage());
        Toast.makeText(dataFragment.getContext(), "Data Error", Toast.LENGTH_SHORT).show();}


}
