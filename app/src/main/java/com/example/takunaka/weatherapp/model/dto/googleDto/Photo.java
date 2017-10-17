
package com.example.takunaka.weatherapp.model.dto.googleDto;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {

    @Nullable
    @SerializedName("photo_reference")
    @Expose
    private String photoReference;

    @Nullable
    public String getPhotoReference() {
        return photoReference;
    }


}
