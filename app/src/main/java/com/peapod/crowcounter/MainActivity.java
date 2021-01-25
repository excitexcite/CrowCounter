package com.peapod.crowcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView; // создали переменную, с помощью которой будем управлять выводом кол-ва ворон
    private int crowNumber; // переменная для хранения кол-ва ворон
    private static final String KEY_COUNT = "crowNumber"; // ключ для хранения пары "ключ - кол-во посчитанных ворон"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.crowNumText); // связали созданную переменную с id с макета
        //проверка чтобы не выводить "Посчитано 0 ворон(ы)" при запуске приложения
        if (savedInstanceState != null) {
            crowNumber = savedInstanceState.getInt(KEY_COUNT, 0); // получили ранее сохраненное кол-во ворон
            if (crowNumber == 0) { // проверка на случай, если пользователь ещё не считал врон, но крутит экран
                return; // если не делать эту проверку, то в таком случае будем получать "Посчитано 0 ворон(ы)"
            }
            textView.setText("Насчитано " + crowNumber + " ворон"); // вывели текст на экран
        }
    }

    // функция для сохранения состояния перед сменой ориентации
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_COUNT, crowNumber); // сохранили кол-во посчитанных ворон
    }

    public void countCrow(View view) {
        crowNumber++; // увеличили кол-во ворон на 1
        textView.setText("Насчитано " + crowNumber + " ворон"); // вывели текст на экран
    }

}