package com.example.voiseassistent;

import android.content.Context;

import com.example.voiseassistent.forecast.ForecastService;
import com.example.voiseassistent.forecast.ForecastToString;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AI {
    private static final Map<String, Consumer<String>> map = new HashMap<>();

    private static void buildInstance(final Context context, final Consumer<String> callback) {
        map.put("привет", s -> callback.accept(
                context.getString(R.string.hiAnswer)));
        map.put("пока", s -> callback.accept(
                context.getString(R.string.byeAnswer)));
        map.put("до свидания", s -> callback.accept(
                context.getString(R.string.byeAnswer)));
        map.put("как дела", s -> callback.accept(
                context.getString(R.string.howAnswer)));
        map.put("как настроение", s -> callback.accept(
                context.getString(R.string.howAnswer)));
        map.put("чем занимаешься", s -> callback.accept(
                context.getString(R.string.whatAnswer)));
        map.put("что делаешь", s -> callback.accept(
                context.getString(R.string.whatAnswer)));

        map.put("какой сегодня день", s -> callback.accept(
                getTodayDay()));
        map.put("какое сегодня число", s -> callback.accept(
                getTodayDay()));
        map.put("сколько времени", s -> callback.accept(
                getTodayTime()));
        map.put("который час", s -> callback.accept(
                getTodayTime()));
        map.put("какой сегодня день недели", s -> callback.accept(
                getTodayDayOfAWeek()));
        map.put("сколько дней до 23 февраля", s -> callback.accept(
                getHowMachDayTo()));

        map.put("погода", s -> getWeather(s, callback));
        map.put("скажи", s -> getNumber(s, callback));
        map.put("что будет", s -> getHoliday(s, callback));
    }

    private static void getHoliday(final String question, final Consumer<String> callback) {
        ForecastToString.getHoliday(
                question.substring(question.lastIndexOf(' ') + 1),
                callback);
    }

    private static void getNumber(final String question, final Consumer<String> callback) {
        Matcher matcher = Pattern.compile("скажи (\\p{N}+)", Pattern.CASE_INSENSITIVE)
                .matcher(question);
        if (matcher.find()) {
            ForecastToString.getNumberForecast(Integer.valueOf(matcher.group(1)), callback);
            return;
        }
        callback.accept(question);
    }

    private static void getWeather(final String question, final Consumer<String> callback) {
        Matcher matcher = Pattern.compile("погода (\\p{L}+)", Pattern.CASE_INSENSITIVE)
                .matcher(question);
        if (matcher.find()) {
            ForecastToString.getWeatherForecast(matcher.group(1), callback);
            return;
        }
        callback.accept("Всё плохо. В вопросе не обнаружен город!!!");
    }

    private static String getTodayDay() {
        return LocalDate.now().toString();
    }

    private static String getTodayTime() {
        return LocalTime.now().toString().substring(0, 8);
    }

    private static String getTodayDayOfAWeek() {
        return LocalDateTime.now().dayOfWeek().getAsText();
    }

    private static String getHowMachDayTo() {
        final DateTime end = new DateTime
                (2023,
                        2,
                        23,
                        0, 0, 0);
        final DateTime start = new DateTime
                (LocalDate.now().year().get(),
                        LocalDate.now().monthOfYear().get(),
                        LocalDate.now().dayOfMonth().get(),
                        0, 0, 0);
        return String.valueOf(Days.daysBetween(new LocalDate(start), new LocalDate(end)).getDays());
    }

    public static void getAnswer(final String question,
                                 final Context context,
                                 final Consumer<String> callback) {
        if (map.size() == 0) {
            buildInstance(context, callback);
        }
        final String normalizedQuestion = question.toLowerCase().trim();

        for (Map.Entry<String, Consumer<String>> entry : map.entrySet()) {
            if (normalizedQuestion.contains(
                    entry.getKey())) {
                entry.getValue().accept(question.toLowerCase().trim());
                return;
            }
        }

        callback.accept(context.getString(R.string.botsAnswer));
    }
}
