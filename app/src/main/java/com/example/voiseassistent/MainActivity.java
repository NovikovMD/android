package com.example.voiseassistent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.EditText;

import com.example.voiseassistent.output.Message;
import com.example.voiseassistent.output.MessageListAdapter;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    protected Button sendButton;
    protected EditText questionText;
    protected RecyclerView chatMessageList;
    protected TextToSpeech textToSpeech;
    protected MessageListAdapter messageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendButton = findViewById(R.id.button);
        questionText = findViewById(R.id.questionField);
        chatMessageList = findViewById(R.id.chatMessageList);
        messageListAdapter = new MessageListAdapter();

        chatMessageList.setLayoutManager(new LinearLayoutManager(this));
        chatMessageList.setAdapter(messageListAdapter);

        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(new Locale("ru"));
            }
        });
        sendButton.setOnClickListener(view -> onSend());
    }

    protected void onSend() {
        messageListAdapter
                .messageList
                .add(new Message(
                questionText.getText().toString() + "\n",
                true));

        AI.getAnswer(
                questionText.getText().toString(),
                this,
                s -> {
                    messageListAdapter
                            .messageList
                            .add(new Message(s + "\n", false));
                    messageListAdapter.notifyDataSetChanged();
                    chatMessageList.scrollToPosition(messageListAdapter.messageList.size() - 1);
                    textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
                });

        questionText.getText().clear();
    }
}