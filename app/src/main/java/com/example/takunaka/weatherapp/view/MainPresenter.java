package com.example.takunaka.weatherapp.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takunaka.weatherapp.R;
import com.example.takunaka.weatherapp.model.dto.api.GoogleApi;
import com.example.takunaka.weatherapp.model.dto.api.GoogleClient;
import com.example.takunaka.weatherapp.model.dto.api.WeatherApi;
import com.example.takunaka.weatherapp.model.dto.api.WeatherClient;
import com.example.takunaka.weatherapp.model.dto.forecastDto.Forecast;
import com.example.takunaka.weatherapp.model.dto.forecastDto.Items;
import com.example.takunaka.weatherapp.model.dto.googleDto.GoogleResponse;
import com.example.takunaka.weatherapp.model.dto.googleDto.Result;
import com.example.takunaka.weatherapp.model.dto.weatherDto.CurrentWeather;
import com.example.takunaka.weatherapp.model.dto.weatherDto.Main;
import com.example.takunaka.weatherapp.model.dto.weatherDto.Weather;
import com.example.takunaka.weatherapp.model.dto.weatherDto.Wind;
import com.example.takunaka.weatherapp.util.Config;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

class MainPresenter {

    private MainView mainView;
    private ImageFragment imageFragment;
    private DataFragment dataFragment;

    private WeatherApi api;
    private GoogleApi googleApi;

    private String city;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);


    MainPresenter(@NonNull MainView mainView,@NonNull DataFragment dataFragment,@NonNull ImageFragment imageFragment) {
        this.mainView = mainView;
        this.imageFragment = imageFragment;
        this.dataFragment = dataFragment;
    }

    void showSearchDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mainView, R.style.Theme_AppCompat_Dialog));
        View view = mainView.getLayoutInflater().inflate(R.layout.dialog_search, null);
        builder.setTitle(R.string.change_city)
                .setView(view)
                .setPositiveButton(R.string.choose, (dialog, which) -> {
                    mainView.cityDialog = (EditText) view.findViewById(R.id.city_input);
                    mainView.cityName.setText(mainView.cityDialog.getText().toString());
                    city = mainView.cityDialog.getText().toString();
                    loadImage();
                    loadData();
                })
                .setNegativeButton(R.string.abort, (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();

    }

    void loadCity(){
        mainView.cityName.setText(" ");
        mainView.cityName.startAnimation(AnimationUtils.loadAnimation(mainView, android.R.anim.slide_in_left));
        mainView.cityName.getAnimation().setStartOffset(1000);
        mainView.layout.startAnimation(AnimationUtils.loadAnimation(mainView, android.R.anim.fade_in));
        mainView.layout.getAnimation().setStartOffset(500);
    }


    public void loadImage() {
        googleApi = GoogleClient.getApi();
        googleApi.getID(city, Config.GOOGLE_PLACES_API_KEY)
                .map(GoogleResponse::getResults)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::showLoad)
                .doAfterTerminate(this::hideLoad)
                .subscribe(this::getPlaceID, throwable -> showError(throwable));
    }


    public void getPlaceID(List<Result> list) {
        String photoReference = list.get(0).getPhotos().get(0).getPhotoReference();
        PhotoTask pt = new PhotoTask(photoReference, imageFragment);
        pt.execute();
    }

    private void showError(Throwable t) {
        Log.e("Error : ", t.getMessage());
        Toast.makeText(imageFragment.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
    }


    public void loadData(){
        api = WeatherClient.getApi();
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
        mainView.progress.setVisibility(View.VISIBLE);
        mainView.progressBarLayout.setVisibility(View.VISIBLE);
    }

    private void hideLoad(){
        mainView.progress.setVisibility(View.INVISIBLE);
        mainView.progressBarLayout.setVisibility(View.INVISIBLE);
    }

}

class PhotoTask extends AsyncTask<String, Void, Bitmap> {

    private String photoReference;
    private ImageFragment imageFragment;

    PhotoTask(String photoRef, ImageFragment imageFragment) {
        this.photoReference = photoRef;
        this.imageFragment = imageFragment;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap image = null;
        String photoUrl = Config.GOOGLE_URL + Config.GOOGLE_PIC + Config.MAXWIDTH
                + "&photoreference=" + photoReference + "&key=" + Config.GOOGLE_PLACES_API_KEY;
        try {
            image = BitmapFactory.decodeStream(new URL(photoUrl).openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }
        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageFragment.img.setImageBitmap(bitmap);
    }
}

