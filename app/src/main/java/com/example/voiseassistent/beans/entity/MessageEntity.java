package com.example.voiseassistent.beans.entity;

import com.example.voiseassistent.beans.Message;

import java.util.Date;

public class MessageEntity {
    public String text;
    public String date;
    public int isSend;

    public MessageEntity(String text, String date, int isSend) {
        this.text = text;
        this.date = date;
        this.isSend = isSend;
    }

    public MessageEntity(Message message){
        text = message.text;
        date = message.date;
        isSend = message.isSend? 1 : 0;
    }
}
