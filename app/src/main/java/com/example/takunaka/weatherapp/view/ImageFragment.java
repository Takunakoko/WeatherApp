package com.example.takunaka.weatherapp.view;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.takunaka.weatherapp.R;

import java.io.IOException;
import java.net.URL;


public class ImageFragment extends Fragment {

    private final ImagePresenter imagePresenter = new ImagePresenter(this);
    public ImageView img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        img = (ImageView) rootView.findViewById(R.id.img_view);

        imagePresenter.loadImage();


        return rootView;
    }






}
