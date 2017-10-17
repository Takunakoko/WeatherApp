
package com.example.takunaka.weatherapp.model.dto.googleDto;

import android.support.annotation.Nullable;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @Nullable
    @SerializedName("photos")
    @Expose
    private List<Photo> photos = null;


    @Nullable
    public List<Photo> getPhotos() {
        return photos;
    }


}
