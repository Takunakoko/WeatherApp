
package com.example.takunaka.weatherapp.model.dto.googleDto;

import android.support.annotation.Nullable;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleResponse {

    @Nullable
    @SerializedName("results")
    @Expose
    private List<Result> results = null;


    @Nullable
    public List<Result> getResults() {
        return results;
    }


}
