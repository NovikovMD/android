package com.example.voiseassistent.output;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voiseassistent.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    protected TextView messageText;
    protected TextView messageDate;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.messageTextView);
        messageDate = itemView.findViewById(R.id.messageDateView);
    }
    public void bind(Message message) {
        messageText.setText(message.text);
        DateFormat fmt = new SimpleDateFormat("mm:hh dd.MM.yy", Locale.GERMAN);
        messageDate.setText(fmt.format(message.date));
    }

}
