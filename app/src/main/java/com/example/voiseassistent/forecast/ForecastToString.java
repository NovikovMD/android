package com.example.voiseassistent.forecast;

import android.util.Log;

import com.example.voiseassistent.beans.Forecast;
import com.example.voiseassistent.beans.ForecastNumber;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastToString {
    public static void getWeatherForecast(String city, final Consumer<String> callback) {
        ForecastService.getWeatherApi()
                .getCurrentWeather(city)
                .enqueue(new Callback<Forecast>() {
                    @Override
                    public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                        Log.w("WEATHER", "Собираю ответ");
                        Forecast result = response.body();
                        if (result != null) {
                            int temp = Math.toIntExact(
                                    Math.round(result.temperatureList.temperature));
                            callback.accept("сейчас где-то "
                                    + temp
                                    + getCelcium(temp)
                                    + " и "
                                    + result.weatherList.get(0).weather_description);
                            Log.w("WEATHER", "Отослал ответ");
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

    public static void getNumberForecast(Integer number, final Consumer<String> callback) {
        ForecastService.getNumbersApi()
                .getNumbers(number)
                .enqueue(new Callback<ForecastNumber>() {
                    @Override
                    public void onResponse(Call<ForecastNumber> call, Response<ForecastNumber> response) {
                        Log.w("Numbers Api", "all cool");
                        ForecastNumber n = response.body();
                        if (n!=null){
                            callback.accept(normalizeNumber(n.numb));
                            return;
                        }
                        callback.accept(String.valueOf(number));
                    }

                    @Override
                    public void onFailure(Call<ForecastNumber> call, Throwable t) {
                        Log.w("Numbers Api", t.getMessage());
                    }
                });
    }

    private static String normalizeNumber(String numb) {
        return numb.replace("рубля 00 копеек","");
    }
}
