package com.example.voiseassistent.forecast;

import com.example.voiseassistent.forecast.parsing.PageAdapter;
import com.google.gson.GsonBuilder;


import okhttp3.HttpUrl;
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
                .build()
                .create(ForecastApi.class);
    }

    public static ForecastApi getNumbersApi() {
        return new Retrofit.Builder()
                .baseUrl("https://htmlweb.ru")
                .addConverterFactory(
                        GsonConverterFactory.create(new GsonBuilder()
                                .setLenient()
                                .create()))
                .build().
                create(ForecastApi.class);
    }

    public static ForecastApi getHoliday() {
        return new Retrofit.Builder()
                .baseUrl(HttpUrl.parse("https://mirkosmosa.ru/holiday/2023/"))
                .addConverterFactory(PageAdapter.FACTORY)
                .build()
                .create(ForecastApi.class);
    }
}
