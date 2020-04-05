package com.karthick.weatherapp.data;

import com.google.gson.annotations.SerializedName;

public class Wind {

    private double speed;

    @SerializedName("deg")
    private double degree;

    public double getSpeed() {
        return speed;
    }

    public double getDegree() {
        return degree;
    }
}