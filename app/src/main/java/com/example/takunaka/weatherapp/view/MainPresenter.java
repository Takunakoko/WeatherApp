package com.example.takunaka.weatherapp.view;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.example.takunaka.weatherapp.R;

class MainPresenter {

    private MainView mainView;

    MainPresenter(@NonNull MainView mainView) {
        this.mainView = mainView;
    }

    void showSearchDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mainView, R.style.Theme_AppCompat_Dialog));
        View view = mainView.getLayoutInflater().inflate(R.layout.dialog_search, null);
        builder.setTitle(R.string.change_city)
                .setView(view)
                .setPositiveButton(R.string.choose, (dialog, which) -> {
                    mainView.cityDialog = (EditText) view.findViewById(R.id.city_input);
                    mainView.cityName.setText(mainView.cityDialog.getText().toString());

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


    void initFragments(){
        mainView.getSupportFragmentManager().beginTransaction()
                .replace(R.id.weatherframe, new DataFragment())
                .commit();

        mainView.getSupportFragmentManager().beginTransaction()
                .replace(R.id.imgframe, new ImageFragment())
                .commit();
    }

}
