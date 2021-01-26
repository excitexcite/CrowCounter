package com.peapod.crowcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView; // создали переменную, с помощью которой будем управлять выводом кол-ва ворон
    private int crowNumber; // переменная для хранения кол-ва ворон
    private static final String KEY_COUNT = "crowNumber"; // ключ для хранения пары "ключ - кол-во посчитанных ворон"
    private SharedPreferences settings; // объект для работы с настройками

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.crowNumText); // связали созданную переменную с id с макета
        //проверка чтобы не выводить "Посчитано 0 ворон(ы)" при запуске приложения
        settings = getSharedPreferences(KEY_COUNT, Context.MODE_PRIVATE); // создали файл с настройками
        if (savedInstanceState != null) {
            crowNumber = savedInstanceState.getInt(KEY_COUNT, 0); // получили ранее сохраненное кол-во ворон
            if (crowNumber == 0) { // проверка на случай, если пользователь ещё не считал врон, но крутит экран
                return; // если не делать эту проверку, то в таком случае будем получать "Посчитано 0 ворон(ы)"
            }
            textView.setText("Насчитано " + crowNumber + " ворон"); // вывели текст на экран
        }
    }

    // функция для отображения "трёх точек" меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // берем данные из ресурсов меню и преобразовываем их в меню на макете
        return true;
    }

    // функция для обработки нажатия на єлементы меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // получили id выбранного пункта меню
        int id = item.getItemId();

        switch (id) {
            // опция открытия окна с заметками
            case R.id.action_note: {
                return true;
            }
            // опция открытия окна настроек
            case R.id.action_settings: {
                return true;
            }
            // опция открытия окна с информацией о программе
            case R.id.action_about: {
                // intent для передачи любых данных между активностями
                Intent intent = new Intent(MainActivity.this, AboutActivity.class); // создали intent
                intent.putExtra("crowNumber", crowNumber); // поместили в него "ключ-значение" кол-во посчитанных вороон
                startActivity(intent); // запустили новую активность
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // функция сохранения данных в файл
    @Override
    protected void onPause() {
        super.onPause();
        // сохраняем данные в файл
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(KEY_COUNT, crowNumber);
        editor.apply();
    }

    // функция считывания данных из файла
    @Override
    protected void onResume() {
        super.onResume();

        if (settings.contains(KEY_COUNT)) {
            crowNumber = settings.getInt(KEY_COUNT, 0); // считываем ранее записанное в файл значение
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