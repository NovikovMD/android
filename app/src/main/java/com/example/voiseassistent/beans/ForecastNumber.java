package com.example.voiseassistent.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ForecastNumber implements Serializable {
    @SerializedName("str")
    @Expose
    public String numb;
}
