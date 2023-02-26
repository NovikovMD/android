package com.example.voiseassistent.forecast;

import android.util.Log;

import com.example.voiseassistent.beans.Forecast;
import com.example.voiseassistent.beans.ForecastNumber;
import com.example.voiseassistent.forecast.parsing.Parser;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.function.Consumer;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastToString {
    public static void getWeatherForecast(final String city, final Consumer<String> callback) {
        ForecastService.getWeatherApi()
                .getCurrentWeather(city)
                .enqueue(new Callback<Forecast>() {
                    @Override
                    public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                        Log.i("WEATHER", "Собираю ответ");
                        if (response.body() != null) {
                            final int temp = Math.toIntExact(
                                    Math.round(response.body().temperatureList.temperature));

                            callback.accept("сейчас где-то "
                                    + temp
                                    + getCelcium(temp)
                                    + " и "
                                    + response.body().weatherList.get(0).weather_description);
                            Log.i("WEATHER", "Отослал ответ");
                            return;
                        }
                        callback.accept("Не могу узнать погоду ^_^");
                    }

                    @Override
                    public void onFailure(Call<Forecast> call, Throwable t) {
                        Log.w("WEATHER", t.getMessage());
                    }
                });
    }

    private static String getCelcium(int temperature) {
        if (Math.abs(temperature) > 9 && Math.abs(temperature) < 21){
            return " градусов ";
        }

        if (Math.abs(temperature%10)<0.001 || Math.abs(temperature%10)>=4.99) {
            return " градусов ";
        }

        if (Math.abs(temperature%10) - 1 < 0.001) {
            return " градус ";
        }

        return " градуса ";
    }

    public static void getNumberForecast(final Integer number, final Consumer<String> callback) {
        ForecastService.getNumbersApi()
                .getNumbers(number)
                .enqueue(new Callback<ForecastNumber>() {
                    @Override
                    public void onResponse(Call<ForecastNumber> call, Response<ForecastNumber> response) {
                        Log.w("Numbers Api", "all cool");
                        if (response.body() !=null){
                            callback.accept(normalizeNumber(response.body().numb));
                            return;
                        }
                        callback.accept(String.valueOf(number));
                    }

                    @Override
                    public void onFailure(Call<ForecastNumber> call, Throwable t) {
                        Log.w("Numbers api", t.getMessage());
                    }
                });
    }

    private static String normalizeNumber(String numb) {
        return numb.replace("рубля 00 копеек","");
    }

    public static void getHoliday(final String date,final Consumer<String> callback) {
        ForecastService.getHoliday()
                .getHoliday(HttpUrl.parse("https://mirkosmosa.ru/holiday/2023/"))
                .enqueue(new Callback<Document>() {
                    @Override
                    public void onResponse(Call<Document> call, Response<Document> response) {
                        if (response.body()!=null) {
                            try {
                                callback.accept(Parser.getHoliday(date, response.body()));
                                return;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        Log.w("HTML", "response body is null.");
                    }

                    @Override
                    public void onFailure(Call<Document> call, Throwable t) {
                        Log.w("HTML", t.getMessage());
                    }
                });
    }
}
