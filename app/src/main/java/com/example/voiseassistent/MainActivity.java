package com.example.voiseassistent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.example.voiseassistent.beans.entity.MessageEntity;
import com.example.voiseassistent.db.DBHelper;
import com.example.voiseassistent.beans.Message;
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

    private DBHelper dBHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageListAdapter = new MessageListAdapter();

        dBHelper = new DBHelper(this);
        database = dBHelper.getWritableDatabase();

        if (savedInstanceState == null) {
            Cursor cursor = database.query(DBHelper.TABLE_MESSAGES,
                    null, null, null, null,
                    null, null);
            if (cursor.moveToFirst()) {
                do {
                    int messageIndex = cursor.getColumnIndex(DBHelper.FIELD_MESSAGE);
                    int dateIndex = cursor.getColumnIndex(DBHelper.FIELD_DATE);
                    int sendIndex = cursor.getColumnIndex(DBHelper.FIELD_SEND);

                    MessageEntity entity = new MessageEntity(cursor.getString(messageIndex),
                            cursor.getString(dateIndex), cursor.getInt(sendIndex));
                    Message message = new Message(entity);
                    messageListAdapter.messageList.add(message);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        isLight = sPref.getBoolean(THEME, true);
        if (!isLight) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        setContentView(R.layout.activity_main);

        sendButton = findViewById(R.id.buttonSend);
        questionText = findViewById(R.id.questionField);
        chatMessageList = findViewById(R.id.chatMessageList);

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
    protected void onDestroy() {
        database.close();
        dBHelper.close();
        super.onDestroy();
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
        editor.apply();

        database.delete(DBHelper.TABLE_MESSAGES, null, null);
        for (Message message : messageListAdapter.messageList) {
            MessageEntity entity = new MessageEntity(message);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.FIELD_MESSAGE, entity.text);
            contentValues.put(DBHelper.FIELD_SEND, entity.isSend);
            contentValues.put(DBHelper.FIELD_DATE, entity.date);
            database.insert(DBHelper.TABLE_MESSAGES, null, contentValues);
        }

        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("Chat", messageListAdapter.messageList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        messageListAdapter.messageList =
                (ArrayList<Message>) savedInstanceState.getSerializable("Chat");
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