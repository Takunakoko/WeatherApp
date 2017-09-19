package com.example.takunaka.weatherapp.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.takunaka.weatherapp.R;
import com.example.takunaka.weatherapp.api.GoogleApi;
import com.example.takunaka.weatherapp.api.GoogleClient;
import com.example.takunaka.weatherapp.util.Cfg;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.Places;

import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private EditText city;
    private TextView cityName;
    private LinearLayout layout;
    private DataFragment dataFragment;
    private ImageFragment imageFragment;


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

        cityName.setText("Moscow");
        cityName.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.slide_in_left));
        cityName.getAnimation().setStartOffset(1000);
        layout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
        layout.getAnimation().setStartOffset(500);




        dataFragment = new DataFragment();
        imageFragment = new ImageFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.imgframe, imageFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.weatherframe, dataFragment)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.city_select) {
            showDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.Theme_AppCompat_Dialog));
        final View view = getLayoutInflater().inflate(R.layout.dialog_search, null);
        builder.setTitle("Выберите город")
                .setView(view)
                .setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        city = (EditText) view.findViewById(R.id.city_input);
                        dataFragment.updateData(city.getText().toString());
                        cityName.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_out));
                        cityName.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cityName.setText(city.getText().toString());
                                cityName.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }



}
