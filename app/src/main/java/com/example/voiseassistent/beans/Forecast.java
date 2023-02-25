package com.example.voiseassistent.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Forecast implements Serializable {
    @SerializedName("weather")
    @Expose
    public List<Weather> weatherList;

    @SerializedName("main")
    @Expose
    public Temperature temperatureList;

    public class Weather{
        @SerializedName("description")
        @Expose
        public String weather_description;
    }
    public class Temperature{
        @SerializedName("temp")
        @Expose
        public Double temperature;
    }
}
