package com.example.voiseassistent.beans;

import com.example.voiseassistent.beans.entity.MessageEntity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message implements Serializable {
    public String text;
    public String date;
    public Boolean isSend;

    public Message(String text, Boolean isSend) {
        this.text = text;
        this.isSend = isSend;
        this.date = new SimpleDateFormat("hh:mm dd.MM.yy", Locale.GERMAN)
                .format(new Date());
    }
    public Message(MessageEntity entity) {
        text = entity.text;
        date = entity.date;
        isSend = entity.isSend == 1;
    }
}
