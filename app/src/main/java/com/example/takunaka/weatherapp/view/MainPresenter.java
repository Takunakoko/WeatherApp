package com.example.takunaka.weatherapp.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takunaka.weatherapp.R;
import com.example.takunaka.weatherapp.model.dto.Data;
import com.example.takunaka.weatherapp.model.dto.api.GoogleClient;
import com.example.takunaka.weatherapp.model.dto.api.WeatherClient;
import com.example.takunaka.weatherapp.model.dto.forecastDto.Items;
import com.example.takunaka.weatherapp.model.dto.googleDto.Photo;
import com.example.takunaka.weatherapp.model.dto.googleDto.Result;
import com.example.takunaka.weatherapp.model.dto.weatherDto.CurrentWeather;
import com.example.takunaka.weatherapp.util.Config;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class MainPresenter {

    //Главная активити
    @NonNull
    private final MainView mainView;
    //Фрагмент изображения
    @NonNull
    private final ImageFragment imageFragment;
    //Фрагмент с данными погоды
    @NonNull
    private final DataFragment dataFragment;
    //константа сохраненного города
    @NonNull
    private final String SAVED_CITY = "saved_city";
    private final String SAVED_DATA = "data";

    //название города
    @Nullable
    private String city;
    private Data data;
    private String url;

    //Формат даты для отображения только дня недели
    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);

    MainPresenter(@NonNull MainView mainView, @NonNull DataFragment dataFragment, @NonNull ImageFragment imageFragment) {
        this.mainView = mainView;
        this.imageFragment = imageFragment;
        this.dataFragment = dataFragment;
    }

    /**
     * метод для показа диалога поиска города
     */
    void showSearchDialog() {
        //создание диалога в dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mainView, R.style.Theme_AppCompat_Dialog));
        View view = mainView.getLayoutInflater().inflate(R.layout.dialog_search, null);
        builder.setTitle(R.string.change_city)
                .setView(view)
                .setPositiveButton(R.string.choose, (dialog, which) -> {
                    //находим поле инпута
                    mainView.cityDialog = (EditText) view.findViewById(R.id.city_input);
                    //присваиваем его переменной
                    city = mainView.cityDialog.getText().toString();
                    //запускаем анимацию
                    setCityName();
                    //запрос к Api
                    loadData();
                })
                .setNegativeButton(R.string.abort, (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();

    }

    /**
     * Метод получения из ответа гугла фотореференс
     * Запуск asyncTask для получения фотографии из ссылки
     *
     * @param list лист с преобразованными ответами от гугла
     */
    private void setImageFromApi(@NonNull List<Result> list) {
        List<Photo> photos = list.get(0).getPhotos();
        if (photos != null) {
            url = Config.GOOGLE_URL + Config.GOOGLE_PIC + Config.MAXWIDTH
                    + "&photoreference=" + photos.get(0).getPhotoReference() + "&key=" + Config.GOOGLE_PLACES_API_KEY;
        }
        //присваивание изображения с помощью библиотеки Picasso
        Picasso.with(imageFragment.getContext())
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageFragment.img, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Picasso.with(imageFragment.getContext())
                                .load(url)
                                .into(imageFragment.img);
                    }
                });
        //скрытие загрузки
        hideLoad();
    }


    /**
     * показ ошибки в случае, если один из запросов не прошел
     *
     * @param t Ошибка
     */
    private void showError(@NonNull Throwable t) {
        //Выводим в logcat ошибку
        Log.e("Error : ", t.getMessage());
        //выводим сообщение об ошибке при запросе
        Toast.makeText(imageFragment.getContext(), "Something went wrong. Try again later", Toast.LENGTH_SHORT).show();
        //скрываем прогресс бар
        hideLoad();
    }

    /**
     * Метод обращения к API
     */
    private void loadData() {
        //Показ прогресс-бара
        showLoad();
        //RX запрос к 3 API и сборка их в объект класса Data для дальнейшей работы
        Observable.combineLatest(WeatherClient.getApi()
                        .getForecast(city, Config.UNITS, Config.FORECAST_API_KEY),
                WeatherClient.getApi().getWeather(city, Config.UNITS, Config.FORECAST_API_KEY),
                GoogleClient.getApi().getID(city, Config.GOOGLE_PLACES_API_KEY),
                (forecast, current, google) -> {
                    data = new Data(current, forecast, google);
                    return data;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getData, this::showError);
    }


    private void getData(Data data) {
        //сохранение полученного бекапа с данными
        saveBackup();
        //раскидывание полученных данных по разным методам
        if (data.getGoogleResponse().getResults() != null) {
            //передача ответа гугла в метод setImageFromApi
            setImageFromApi(data.getGoogleResponse().getResults());
        }
        if (data.getForecast().getList() != null) {
            //передача подкаста в метод weekForecast
            weekForecast(data.getForecast().getList());
        }
        if (data.getCurrentWeather() != null) {
            //передача данных о погоде сегодня из ответа в метод setMainData
            setMainData(data.getCurrentWeather());
        }
    }

    /**
     * Анимация при изменении названия города
     */
    private void setCityName() {
        //старт анимации угасания
        mainView.cityName.startAnimation(AnimationUtils.loadAnimation(mainView, android.R.anim.fade_out));
        //включение прослушки анимации
        mainView.cityName.getAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //когда анимация заканчивается устанавливать новое значение
                mainView.cityName.setText(city);
                //включать анимацию появления
                mainView.cityName.setAnimation(AnimationUtils.loadAnimation(mainView, android.R.anim.fade_in));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * @param day      TextView дня
     * @param dayTemp  TextView температуры
     * @param dayIcon  ImageView для погоды на этот день
     * @param dateTime Дата из ответа в unix формате
     * @param temp     температура из ответа
     * @param desc     описание из ответа (для отправки в метод returnImg)
     */
    private void forecastDayTemp(@NonNull TextView day, @NonNull TextView dayTemp, @NonNull ImageView dayIcon, int dateTime, double temp, @NonNull String desc) {
        //установка иконки погоды
        dayIcon.setBackgroundResource(returnImg(desc));
        //установка даты с преобразованием через SDF
        day.setText(sdf.format(new Date(dateTime * 1000L)));
        //установка температуры на этот день
        dayTemp.setText(String.valueOf((int) temp + "°С"));
    }

    /**
     * метод установки
     * иконки погоды
     * температуры и основных данных
     * по сегодняшнему дню
     *
     * @param currentWeather класс приходящий в ответ от API
     */
    private void setMainData(@NonNull CurrentWeather currentWeather) {
        //установка температуры
        mainView.tempText.setText(String.valueOf((int) currentWeather.getMain().getTemp() + "°C"));
        //установка давления
        dataFragment.pressure.setText(String.valueOf((int) (currentWeather.getMain().getPressure() * 0.75006375541921) + "mm"));
        //минимум температуры
        dataFragment.maxTemp.setText(String.valueOf((int) currentWeather.getMain().getTempMax() + "°C"));
        //максимум температуры
        dataFragment.minTemp.setText(String.valueOf((int) currentWeather.getMain().getTempMin() + "°C"));
        //установка данных по ветру
        dataFragment.wind.setText(String.valueOf((int) currentWeather.getWind().getSpeed() + "m/s"));
        //установка картинки с использованием метода returnImg
        if (currentWeather.getWeather() != null) {
            mainView.weather_icon.setBackgroundResource(returnImg(currentWeather.getWeather().get(0).getMain()));
        }
    }

    /**
     * метод установки погоды на неделю
     *
     * @param items входящий лист с объектами API
     */
    private void weekForecast(@NonNull List<Items> items) {
        //с помощью метода forecastDayTemp устанавливаем погоду, дату и картинку
        List<com.example.takunaka.weatherapp.model.dto.forecastDto.Weather> weather = items.get(0).getWeather();
        if (weather != null) {
            forecastDayTemp(dataFragment.day1, dataFragment.day1_temp, dataFragment.day1_img,
                    items.get(0).getDt(), items.get(0).getTemp().getMax(), weather.get(0).getMain());
            forecastDayTemp(dataFragment.day2, dataFragment.day2_temp, dataFragment.day2_img,
                    items.get(1).getDt(), items.get(1).getTemp().getMax(), weather.get(0).getMain());
            forecastDayTemp(dataFragment.day3, dataFragment.day3_temp, dataFragment.day3_img,
                    items.get(2).getDt(), items.get(2).getTemp().getMax(), weather.get(0).getMain());
            forecastDayTemp(dataFragment.day4, dataFragment.day4_temp, dataFragment.day4_img,
                    items.get(3).getDt(), items.get(3).getTemp().getMax(), weather.get(0).getMain());
            forecastDayTemp(dataFragment.day5, dataFragment.day5_temp, dataFragment.day5_img,
                    items.get(4).getDt(), items.get(4).getTemp().getMax(), weather.get(0).getMain());
            forecastDayTemp(dataFragment.day6, dataFragment.day6_temp, dataFragment.day6_img,
                    items.get(5).getDt(), items.get(5).getTemp().getMax(), weather.get(0).getMain());
            forecastDayTemp(dataFragment.day7, dataFragment.day7_temp, dataFragment.day7_img,
                    items.get(6).getDt(), items.get(6).getTemp().getMax(), weather.get(0).getMain());

        }
    }

    /**
     * метод подбора картинки
     * на основании входящего из api описания подбирает нужную картинку
     *
     * @param description описание
     * @return возвращает id картинки
     */
    private int returnImg(@NonNull String description) {
        switch (description) {
            case "Drizzle":
            case "Rain":
                return R.drawable.ic_rain;
            case "Thunderstorm":
                return R.drawable.ic_storm;
            case "Snow":
                return R.drawable.ic_snowflake;
            case "Clear":
                return R.drawable.ic_sunny;
            case "Clouds":
                return R.drawable.ic_clouds;
            default:
                return R.drawable.ic_sunny;
        }
    }

    /**
     * проверка подключения к интернету
     *
     * @return возвращает true если подключения к интернету нет
     */
    boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) mainView.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        return netInfo == null;
    }

    /**
     * Показ прогресс-бара
     */
    private void showLoad() {
        //установка видимости layout
        mainView.progress.setVisibility(View.VISIBLE);
        //установка видимости прогресс-бара
        mainView.progressBarLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Скрытие прогресс-бара
     */
    private void hideLoad() {
        //установка видимости layout
        mainView.progress.setVisibility(View.INVISIBLE);
        //установка видимости прогресс-бара
        mainView.progressBarLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * Загрузка данных по последнему городу
     */
    void initData() {
        loadCity();
        loadData();
    }

    /**
     * Сохранение города при закрытии приложения
     */
    private void saveBackup() {
        //создание контейнера для хранения
        SharedPreferences.Editor ed = mainView.sPref.edit();
        //помещение пользователя в хранилище
        ed.putString(SAVED_CITY, city);
        //сериализация класса Data для сохранения в SP
        ed.putString(SAVED_DATA, new Gson().toJson(data));
        ed.apply();
    }

    /**
     * Загрузка города из сохраненного SharedPreferences
     */

    void loadCity() {
        //установка сохраненного города в переменную
        city = mainView.sPref.getString(SAVED_CITY, "");
        //запуск анимации
        setCityName();
    }

    /**
     * Метод загрузки последних кэшированных данных при отсутствии интернета
     */
    void loadBackup() {
        //показ сообщения об отсутвтии интернета
        new AlertDialog.Builder(mainView)
                .setTitle(R.string.no_internet_conn)
                .setPositiveButton(R.string.ok, null).show();
        //получение данных из сериализованного класса Data
        Data obj = new Gson().fromJson(mainView.sPref.getString(SAVED_DATA, ""), Data.class);
        //проверка получен ли объект obj
        if (obj != null) {
            //раскидывание кешированных данных по методам
            if (obj.getGoogleResponse().getResults() != null) {
                setImageFromApi(obj.getGoogleResponse().getResults());
            }
            if (obj.getForecast().getList() != null) {
                weekForecast(obj.getForecast().getList());
            }
            if (obj.getCurrentWeather() != null) {
                setMainData(obj.getCurrentWeather());
            }
        }
    }

    /**
     * проверка на первый запуск приложения
     *
     * @return возвращает true если это первый запуск
     */
    boolean firstLoad() {
        return mainView.sPref.contains(SAVED_CITY);
    }
}


