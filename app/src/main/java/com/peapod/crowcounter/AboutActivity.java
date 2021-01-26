package com.peapod.crowcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private TextView aboutTextView; // переменная для текстового поле вывода информации о программе
    private int crowNumber; // переменная для хранения кол-ва посчитанных ворон

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        aboutTextView = findViewById(R.id.aboutText); // связали переменную с id с макета
        Intent intent = getIntent(); // получили intent из главного окна
        crowNumber = intent.getIntExtra("crowNumber", 0); // получили кол-во ворон
        String strFormat = getResources().getString(R.string.about_info); // получили строку из string.xml без подстановки
        String strMessage = String.format(strFormat, crowNumber); // выполнили подстановку числа
        aboutTextView.setText(strMessage); // вывели текст на экран

    }
}