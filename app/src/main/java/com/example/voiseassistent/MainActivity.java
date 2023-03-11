package com.example.voiseassistent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.example.voiseassistent.output.Message;
import com.example.voiseassistent.output.MessageListAdapter;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    protected Button sendButton;
    protected EditText questionText;
    protected RecyclerView chatMessageList;
    protected TextToSpeech textToSpeech;
    protected MessageListAdapter messageListAdapter;
    public static final String APP_PREFERENCES = "mysettings";
    protected SharedPreferences sPref;
    private boolean isLight = true;
    private String THEME = "THEME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sPref = getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE);
        isLight = sPref.getBoolean(THEME, true);
        if (!isLight) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.day_settings:
                isLight = true;
                THEME = "DAY";
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case R.id.night_settings:
                isLight = false;
                THEME = "NIGHT";
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(THEME, isLight);
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("Chat",messageListAdapter.messageList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        messageListAdapter.messageList = (ArrayList<Message>)
                savedInstanceState.getSerializable("Chat");
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