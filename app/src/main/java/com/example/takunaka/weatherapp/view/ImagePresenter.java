package com.example.takunaka.weatherapp.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.takunaka.weatherapp.model.dto.api.GoogleApi;
import com.example.takunaka.weatherapp.model.dto.api.GoogleClient;
import com.example.takunaka.weatherapp.model.dto.googleDto.GoogleResponse;
import com.example.takunaka.weatherapp.model.dto.googleDto.Result;
import com.example.takunaka.weatherapp.util.Config;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;



public class ImagePresenter {


    private ImageFragment imageFragment;
    private GoogleApi googleApi;

    public ImagePresenter(@NonNull ImageFragment imageFragment) {
        this.imageFragment = imageFragment;
        googleApi = GoogleClient.getApi();
    }

    public void loadImage() {
        googleApi.getID("Moscow", Config.GOOGLE_PLACES_API_KEY)
                .map(GoogleResponse::getResults)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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


