package com.example.takunaka.weatherapp.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.takunaka.weatherapp.R;

public class MainView extends AppCompatActivity {

    public EditText cityDialog;
    public TextView cityName;
    public ProgressBar progress;
    public LinearLayout progressBarLayout;
    public TextView tempText;
    public ImageView weather_icon;
    private final DataFragment dataFragment = new DataFragment();
    private final ImageFragment imageFragment = new ImageFragment();
    private final MainPresenter mainPresenter = new MainPresenter(this, dataFragment, imageFragment);
    public SharedPreferences sPref;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //установка тулбара
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        //проверка бара на null
        if (bar != null){
            //установка title и subtitle на null
            bar.setTitle(null);
            bar.setSubtitle(null);
        }
        //Инициализация SharedPreferences
        sPref = getPreferences(MODE_PRIVATE);
        //Название города на плашке
        cityName = (TextView) findViewById(R.id.city_name_text);
        //прогресс-бар
        progress = (ProgressBar) findViewById(R.id.loading);
        //лэйаут прогресс бара с затемнением
        progressBarLayout = (LinearLayout) findViewById(R.id.progressBar_layout);
        //температура на плашке
        tempText = (TextView) findViewById(R.id.temp_text);
        //иконка погоды в данный момент на плашке
        weather_icon = (ImageView) findViewById(R.id.current_weather_img);

        //старт фрагментов
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.weatherframe, dataFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.imgframe, imageFragment)
                .commit();

        //проверка savedInstanceState после восстановления состояния
        if (savedInstanceState != null){
            //если не нулевое - проверка констатны города в SharedPreferences
            mainPresenter.checkState();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.city_select) {
            //вызов метода, показывающего диалог пользователя
            mainPresenter.showSearchDialog();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //сохранение города
        mainPresenter.saveCity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //проверка и восстановление города
        mainPresenter.checkState();
    }


}
