package com.example.voiseassistent.forecast.parsing;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Parser {

    public static final String WRONG_DATE = "Введена какая-то кривая дата -_-";

    public static String getHoliday(final String date, final Document content) throws IOException {
        final Elements list = content.body()
                .getElementsByAttributeValue("id", "holiday_calend")
                .first()
                .children();

        return selectMonth(date, list);
    }


    private static String selectMonth(final String date, final Elements list) {
        for (final Element monthSelect : list) {
            if (monthSelect.getElementsByTag("a")
                    .first()
                    .text()
                    .contains(normalizeMonth(date).substring(0, 4))) {
                return selectDay(date, monthSelect);
            }
        }
        return WRONG_DATE;
    }

    private static String selectDay(final String date, final Element monthSelect) {
        final String normalizedDate = date.substring(0, date.indexOf('.')) + normalizeMonth(date);

        for (final Element daySelect : monthSelect.children()
                .get(1)
                .children()) {
            if (daySelect.getElementsByTag("span")
                    .first()
                    .text()
                    .contains(normalizedDate)) {
                return getOutput(daySelect);
            }
        }
        return WRONG_DATE;
    }

    private static String normalizeMonth(final String date) {
        String test = date.substring(date.indexOf('.') + 1);
        switch (test) {
            case "01":
                return " января";
            case "02":
                return " февраля";
            case "03":
                return " марта";
            case "04":
                return " апреля";
            case "05":
                return " мая";
            case "06":
                return " июня";
            case "07":
                return " июля";
            case "08":
                return " августа";
            case "09":
                return " сентября";
            case "10":
                return " октября";
            case "11":
                return " ноября";
            case "12":
                return " декабря";
            default:
                return "бе-бе";
        }
    }

    private static String getOutput(Element daySelect) {
        if (daySelect.getElementsByTag("a").first() != null) {
            return daySelect.getElementsByTag("a").first().text();
        }
        return "Обычный день";
    }
}
