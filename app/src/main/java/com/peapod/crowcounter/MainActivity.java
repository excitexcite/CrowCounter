package com.peapod.crowcounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
    // название канала для уведомлений
    private static final String NOTIFICATIONS_CHANEL = "CrowСounter";
    // id уведомления для счёта ворон
    private static final int COUNTCROW_ID = 101;
    // id уведомления для счёта достижения максимума счёта ворон
    private static final int CROWLIMIT_ID = 102;
    // переменная для хранения состояния вкл/выкл соответствуюзей настройки
    private boolean notificationOption = false;
    // создадим переменные c названиями ключей (как на макете root_preference.xml) для определния в switch измененной настройки
    public static final String CROW_LIMIT = "crow_limit";
    public static final String NOTIFICATIONS_KEY = "notifications";
    // переменная для хранения значения лимита ворон до его изменения
    private int maxCrowNum = 10000;

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
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class); // создали intent
                startActivity(intent); // запустили новую активность (активность с настройками)
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

        // создали объект для управления настройками
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //выполняем проверки на факт включенной настройки

        if (preferences.contains(NOTIFICATIONS_KEY)) {
            notificationOption = preferences.getBoolean(NOTIFICATIONS_KEY, false);
        }
        if (preferences.contains(CROW_LIMIT)) {
            maxCrowNum = Integer.parseInt(preferences.getString(CROW_LIMIT, String.valueOf(maxCrowNum)));
        }

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
        if (crowNumber >= maxCrowNum) {
            // PendingIntent используется для перехода между на другой экран из уведомления
            PendingIntent intent = PendingIntent.getActivity(
                    this,
                    0,
                    new Intent(MainActivity.this, SettingsActivity.class),
                    PendingIntent.FLAG_CANCEL_CURRENT);
            // упаковали все нужное в наше уведомление
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, NOTIFICATIONS_CHANEL)
                            .setSmallIcon(R.drawable.crow)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText("Достигнут лимит счёта ворон, все, приехали!")
                            .setStyle(new NotificationCompat.InboxStyle())
                            .setPriority(NotificationCompat.FLAG_INSISTENT)
                            .setContentIntent(intent)
                            .addAction(R.drawable.crow, "В настройки", intent)
                            .setAutoCancel(true)
                            .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND);
            // создание необходимо элементов для канала уведомлений (Notification Chanel)
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            // с помощью NotificationManagerCompat построили и отобразили уведомление
            NotificationManagerCompat notificationManagerCompat =
                    NotificationManagerCompat.from(this);
            NotificationChannel channel = null;
            // канал нужен для Android 8.0+
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                channel = new NotificationChannel(CROW_LIMIT, name, importance);
            }
            notificationManagerCompat.createNotificationChannel(channel);
            notificationManagerCompat.notify(CROWLIMIT_ID, builder.build());
            return;
        }

        crowNumber++; // увеличили кол-во ворон на 1
        textView.setText("Насчитано " + crowNumber + " ворон"); // вывели текст на экран

        if (notificationOption) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, NOTIFICATIONS_CHANEL)
                            .setSmallIcon(R.drawable.crow)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText("Насчитано " + crowNumber + " ворон")
                            .setStyle(new NotificationCompat.InboxStyle())
                            .setPriority(NotificationCompat.FLAG_INSISTENT)
                            .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND);
            // создание необходимо элементов для канала уведомлений (Notification Chanel)
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            // с помощью NotificationManagerCompat построили и отобразили уведомление
            NotificationManagerCompat notificationManagerCompat =
                    NotificationManagerCompat.from(this);
            NotificationChannel channel = null;
            // канал нужен для Android 8.0+
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                channel = new NotificationChannel(NOTIFICATIONS_CHANEL, name, importance);
            }
            notificationManagerCompat.createNotificationChannel(channel);
            notificationManagerCompat.notify(COUNTCROW_ID, builder.build());
        }
    }
}