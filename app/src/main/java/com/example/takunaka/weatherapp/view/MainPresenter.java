package com.example.takunaka.weatherapp.view;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import com.example.takunaka.weatherapp.model.dto.api.GoogleClient;
import com.example.takunaka.weatherapp.model.dto.api.WeatherClient;
import com.example.takunaka.weatherapp.model.dto.forecastDto.Forecast;
import com.example.takunaka.weatherapp.model.dto.forecastDto.Items;
import com.example.takunaka.weatherapp.model.dto.googleDto.GoogleResponse;
import com.example.takunaka.weatherapp.model.dto.googleDto.Photo;
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
    //название города
    @Nullable
    private String city;
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
                    //запускаем запрос к GoogleApi
                    getPhotoReference();
                    //запрос к WeatherApi
                    loadData();
                })
                .setNegativeButton(R.string.abort, (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();

    }

    /**
     * метод получения ID изображения из API гугла
     */
    private void getPhotoReference() {
        //показ прогресс-бара
        showLoad();
        //запрос в гугл для получения идентификатора
        GoogleClient.getApi().getID(city, Config.GOOGLE_PLACES_API_KEY)
                //преобразование результатов
                .map(GoogleResponse::getResults)
                //создание нового потока
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //если данные получены - вызываем метод getPlaceID , если нет - показываем ошибку
                .subscribe(this::getPlaceID, this::showError);
    }

    /**
     * Метод получения из ответа гугла фотореференс
     * Запуск asyncTask для получения фотографии из ссылки
     *
     * @param list лист с преобразованными ответами от гугла
     */
    private void getPlaceID(@NonNull List<Result> list) {
        String photoReference = null;
        //присваивание референса в временную переменную
        List<Photo> photos = list.get(0).getPhotos();
        if (photos != null) {
            photoReference = photos.get(0).getPhotoReference();
        }

        //Создание новой асинхронной задачи с преобразованием изображения в bitmap
        PhotoTask pt = new PhotoTask(photoReference, imageFragment, this);
        //старт задачи
        pt.execute();
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
     * проверка состояния SharedPreferences
     */
    void checkState() {
        //если содержат константу SAVED_CITY
        if (mainView.sPref.contains(SAVED_CITY)) {
            //загружать последний город
            loadCity();
        }
    }

    /**
     * Метод обращения к OpenWeatherAPI
     */
    private void loadData() {
        //Показ прогресс-бара
        showLoad();
        // обращение к API для получения листа с данными на неделю
        WeatherClient.getApi().getForecast(city, Config.UNITS, Config.FORECAST_API_KEY)
                .map(Forecast::getList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::callForecast, this::showError);

        //обращение к API для получения температуры на сегодня
        WeatherClient.getApi().getWeather(city, Config.UNITS, Config.FORECAST_API_KEY)
                .map(CurrentWeather::getMain)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setMain, this::showError);

        //обращение к API для получения ветра на сегодня
        WeatherClient.getApi().getWeather(city, Config.UNITS, Config.FORECAST_API_KEY)
                .map(CurrentWeather::getWind)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setWind, this::showError);

        //обращение к API для получения погоды на сегодня
        WeatherClient.getApi().getWeather(city, Config.UNITS, Config.FORECAST_API_KEY)
                .map(CurrentWeather::getWeather)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setPic, this::showError);
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
     * @param day TextView дня
     * @param dayTemp TextView температуры
     * @param dayIcon ImageView для погоды на этот день
     * @param dateTime Дата из ответа в unix формате
     * @param temp температура из ответа
     * @param desc описание из ответа (для отправки в метод returnImg)
     */
    private void forecastDayTemp(@NonNull TextView day, @NonNull TextView dayTemp, @NonNull ImageView dayIcon, @NonNull int dateTime, @NonNull double temp, @NonNull String desc) {
        //установка иконки погоды
        dayIcon.setBackgroundResource(returnImg(desc));
        //установка даты с преобразованием через SDF
        day.setText(sdf.format(new Date(dateTime * 1000L)));
        //установка температуры на этот день
        dayTemp.setText(String.valueOf((int) temp + "°С"));
    }

    /**
     * Метод установки базовых параметров сегодняшней погоды
     *
     * @param main объект из ответа API
     */
    private void setMain(@NonNull Main main) {
        //установка температуры
        mainView.tempText.setText(String.valueOf((int) main.getTemp() + "°C"));
        //установка давления
        dataFragment.pressure.setText(String.valueOf((int) (main.getPressure() * 0.75006375541921) + "mm"));
        //минимум температуры
        dataFragment.maxTemp.setText(String.valueOf((int) main.getTempMax() + "°C"));
        //максимум температуры
        dataFragment.minTemp.setText(String.valueOf((int) main.getTempMin() + "°C"));
    }

    /**
     * Метод установки ветренности
     *
     * @param w объект API
     */
    private void setWind(@NonNull Wind w) {
        //установка данных по ветру
        dataFragment.wind.setText(String.valueOf((int) w.getSpeed() + "m/s"));
    }

    /**
     * метод установки центральной иконки погоды
     *
     * @param list лист приходящий из ответа API
     */
    private void setPic(@NonNull List<Weather> list) {
        //установка картинки с использованием метода returnImg
        mainView.weather_icon.setBackgroundResource(returnImg(list.get(0).getMain()));
    }

    /**
     * метод установки погоды на неделю
     *
     * @param items входящий лист с объектами API
     */
    private void callForecast(@NonNull List<Items> items) {
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
    void hideLoad() {
        //установка видимости layout
        mainView.progress.setVisibility(View.INVISIBLE);
        //установка видимости прогресс-бара
        mainView.progressBarLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * Сохранение города при закрытии приложения
     */
    void saveCity() {
        //создание контейнера для хранения
        SharedPreferences.Editor ed = mainView.sPref.edit();
        //помещение пользователя в хранилище
        ed.putString(SAVED_CITY, city);
        ed.apply();
    }

    /**
     * Загрузка города из сохраненного SharedPreferences
     */
    private void loadCity() {
        //установка сохраненного города в переменную
        city = mainView.sPref.getString(SAVED_CITY, "");
        //запуск анимации
        setCityName();
        //получение изображения
        getPhotoReference();
        //получение данных погоды
        loadData();
    }

}

/**
 * Асинхронная задача по получению фотографии
 */
class PhotoTask extends AsyncTask<String, Void, Bitmap> {

    private final String photoReference;
    private final ImageFragment imageFragment;
    private final MainPresenter mainPresenter;

    PhotoTask(String photoRef, ImageFragment imageFragment, MainPresenter mainPresenter) {
        this.photoReference = photoRef;
        this.imageFragment = imageFragment;
        this.mainPresenter = mainPresenter;
    }

    @Nullable
    @Override
    protected Bitmap doInBackground(String... params) {
        //Инициализация битмапа
        Bitmap image = null;
        //Создание урла фотографии
        String photoUrl = Config.GOOGLE_URL + Config.GOOGLE_PIC + Config.MAXWIDTH
                + "&photoreference=" + photoReference + "&key=" + Config.GOOGLE_PLACES_API_KEY;
        try {
            //преобразование изображения из ссылки в Bitmap
            image = BitmapFactory.decodeStream(new URL(photoUrl).openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //возврат изображения
        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        //если задача прошла успешно ставим bitmap фрагменту изображения
        imageFragment.img.setImageBitmap(bitmap);
        //скрываем прогресс-бар
        mainPresenter.hideLoad();
    }
}

