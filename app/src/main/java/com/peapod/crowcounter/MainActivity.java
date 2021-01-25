package com.peapod.crowcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView; // создали переменную, с помощью которой будем управлять выводом кол-ва ворон
    private int crowNumber; // переменная для хранения кол-ва ворон


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.crowNumText); // связали созданную переменную с id с макета
    }

    public void countCrow(View view) {
        crowNumber++; // увеличили кол-во ворон на 1
        textView.setText("Насчитано " + crowNumber + " ворон"); // вывели текст на экран
    }

}