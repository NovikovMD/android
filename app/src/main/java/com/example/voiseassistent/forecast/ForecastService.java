package com.example.voiseassistent.forecast;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastService {
    public static ForecastApi getWeatherApi() {
        return new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org")
                .addConverterFactory(
                        GsonConverterFactory.create(new GsonBuilder()
                                .setLenient()
                                .create()))
                .build().create(ForecastApi.class);
    }

    public static ForecastApi getNumbersApi() {
        return new Retrofit.Builder()
                .baseUrl("https://htmlweb.ru")
                .addConverterFactory(
                        GsonConverterFactory.create(new GsonBuilder()
                                .setLenient()
                                .create()))
                .build().create(ForecastApi.class);
    }

}
