package com.example.takunaka.weatherapp.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.takunaka.weatherapp.R;

public class MainView extends AppCompatActivity {

    public EditText cityDialog;
    public TextView cityName;
    public LinearLayout layout;
    public ProgressBar progress;
    public LinearLayout progressBarLayout;
    private DataFragment dataFragment = new DataFragment();
    private ImageFragment imageFragment = new ImageFragment();
    private MainPresenter mainPresenter = new MainPresenter(this, dataFragment, imageFragment);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setSubtitle(null);

        cityName = (TextView) findViewById(R.id.city_name_text);
        layout = (LinearLayout) findViewById(R.id.fade_layout);
        progress = (ProgressBar) findViewById(R.id.loading);
        progressBarLayout = (LinearLayout) findViewById(R.id.progressBar_layout);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.weatherframe, dataFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.imgframe, imageFragment)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.city_select) {
            mainPresenter.showSearchDialog();
        }
        return super.onOptionsItemSelected(item);
    }

}
