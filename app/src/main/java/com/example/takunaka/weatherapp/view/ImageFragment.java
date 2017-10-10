package com.example.takunaka.weatherapp.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.takunaka.weatherapp.R;


public class ImageFragment extends Fragment {

    public ImageView img;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        //ImageView картинки города
        img = (ImageView) rootView.findViewById(R.id.img_view);
        return rootView;
    }


}
