package com.example.voiseassistent.forecast;

import com.example.voiseassistent.beans.Forecast;
import com.example.voiseassistent.beans.ForecastNumber;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForecastApi {
    @GET("/data/2.5/weather?appid=a54d0e4c2de37393b36b7f1339ac891b&units=metric&lang=ru")
    Call<Forecast> getCurrentWeather(@Query("q") String city);

    @GET("/json/convert/num2str?")
    Call<ForecastNumber> getNumbers(@Query("num") Integer number);
}
