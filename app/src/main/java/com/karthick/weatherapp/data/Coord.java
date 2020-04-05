package com.karthick.weatherapp.data;

import com.google.gson.annotations.SerializedName;

public class Coord {

    @SerializedName("lon")
    private String longitude;

    @SerializedName("lat")
    private String latitude;

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }
}
