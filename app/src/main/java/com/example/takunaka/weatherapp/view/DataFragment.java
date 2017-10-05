package com.example.takunaka.weatherapp.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.takunaka.weatherapp.R;


public class DataFragment extends Fragment {
    private View rootView;
    private final DataPresenter dataPresenter = new DataPresenter(this);

    public ImageView image;
    public TextView temp;
    public TextView minTemp;
    public TextView maxTemp;
    public TextView pressure;
    public TextView wind;
    public TextView day1;
    public TextView day2;
    public TextView day3;
    public TextView day4;
    public TextView day5;
    public TextView day6;
    public TextView day7;
    public TextView day1_temp;
    public TextView day2_temp;
    public TextView day3_temp;
    public TextView day4_temp;
    public TextView day5_temp;
    public TextView day6_temp;
    public TextView day7_temp;
    public ImageView day1_img;
    public ImageView day2_img;
    public ImageView day3_img;
    public ImageView day4_img;
    public ImageView day5_img;
    public ImageView day6_img;
    public ImageView day7_img;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_data, container, false);
        initViews();
        dataPresenter.loadData("Moscow");
        return rootView;
    }

    public void initViews(){
        day1 = (TextView) rootView.findViewById(R.id.day1);
        day2 = (TextView) rootView.findViewById(R.id.day2);
        day3 = (TextView) rootView.findViewById(R.id.day3);
        day4 = (TextView) rootView.findViewById(R.id.day4);
        day5 = (TextView) rootView.findViewById(R.id.day5);
        day6 = (TextView) rootView.findViewById(R.id.day6);
        day7 = (TextView) rootView.findViewById(R.id.day7);
        day1_temp = (TextView) rootView.findViewById(R.id.day1_temp);
        day2_temp = (TextView) rootView.findViewById(R.id.day2_temp);
        day3_temp = (TextView) rootView.findViewById(R.id.day3_temp);
        day4_temp = (TextView) rootView.findViewById(R.id.day4_temp);
        day5_temp = (TextView) rootView.findViewById(R.id.day5_temp);
        day6_temp = (TextView) rootView.findViewById(R.id.day6_temp);
        day7_temp = (TextView) rootView.findViewById(R.id.day7_temp);
        day1_img = (ImageView) rootView.findViewById(R.id.day1_icon);
        day2_img = (ImageView) rootView.findViewById(R.id.day2_icon);
        day3_img = (ImageView) rootView.findViewById(R.id.day3_icon);
        day4_img = (ImageView) rootView.findViewById(R.id.day4_icon);
        day5_img = (ImageView) rootView.findViewById(R.id.day5_icon);
        day6_img = (ImageView) rootView.findViewById(R.id.day6_icon);
        day7_img = (ImageView) rootView.findViewById(R.id.day7_icon);
        image = (ImageView) rootView.findViewById(R.id.current_weather_img);
        temp = (TextView) rootView.findViewById(R.id.current_temp);
        minTemp = (TextView) rootView.findViewById(R.id.min_temp);
        maxTemp = (TextView) rootView.findViewById(R.id.max_temp);
        pressure = (TextView) rootView.findViewById(R.id.pressure);
        wind = (TextView) rootView.findViewById(R.id.wind);
    }
}
