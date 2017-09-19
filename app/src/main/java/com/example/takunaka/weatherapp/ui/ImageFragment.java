package com.example.takunaka.weatherapp.ui;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.takunaka.weatherapp.R;
import com.example.takunaka.weatherapp.api.GoogleApi;
import com.example.takunaka.weatherapp.api.GoogleClient;
import com.example.takunaka.weatherapp.dto.googleDto.GoogleResponse;
import com.example.takunaka.weatherapp.dto.googleDto.Result;
import com.example.takunaka.weatherapp.util.Cfg;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class ImageFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApi googleApi;
    private GoogleApiClient mGoogleApiClient;
    private ImageView img;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        img = (ImageView) rootView.findViewById(R.id.img_view);

        googleApi = GoogleClient.getApi();

        googleApi.getID("moscow", Cfg.getInstance().getGooglePlacesApiKey())
                .map(GoogleResponse::getResults)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getPicture);

        return rootView;

    }

    public void getPicture(List<Result> list){
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        String placeID = list.get(0).getPhotos().get(0).getPhotoReference();

        PlacePhotoMetadataResult result = Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeID).await();

        if(result != null && result.getStatus().isSuccess()){
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            PlacePhotoMetadata photo = photoMetadataBuffer.get(0);

            Bitmap image = photo.getPhoto(mGoogleApiClient).await().getBitmap();

            img.setImageBitmap(image);

        }

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "FUCK", Toast.LENGTH_SHORT).show();
    }
}
