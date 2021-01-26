package com.peapod.crowcounter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    // создание окна с настройками
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity); // подстановка окна настроек пока ещё без окна root_preferences.xml
        // подстановка root_preferences.xml в пустое окно настроек
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment()) // момент подстановки
                    .commit();
        }
        // добавление панели инструментов
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // класс, отвечающий за создание окна root_preferences.xml
    public static class SettingsFragment extends PreferenceFragmentCompat {
        // создали "стукача"
        private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
        // создадим переменные c названиями ключей (как на макете root_preference.xml) для определения в switch измененной настройки
        public static final String CROW_LIMIT = "crow_limit";
        public static final String NOTIFICATIONS_KEY = "notifications";
        // переменная для хранения значения лимита ворон до его изменения
        private int maxCrowNum = 10000;
        // переменная для определения наличия ошибки при вводе лимита ворон
        boolean editTextError = false;

        @Override
        public void onCreatePreferences(final Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // вот тут он начинает подслушивать, не изменилось ли что
            preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                // а вот тут он бежит "стучать" при малейшем изменении
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    switch (key) {
                        case NOTIFICATIONS_KEY: {
                            if (sharedPreferences.getBoolean(NOTIFICATIONS_KEY, false)) {
                                Toast toast = Toast.makeText(getActivity(), "Уведомления включены", Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(getActivity(), "Уведомления выключены", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            break;
                        }
                        case CROW_LIMIT: {
                            try {
                                // считали из файла измененное значение настроек
                                maxCrowNum = Integer.parseInt(sharedPreferences.getString(CROW_LIMIT, Integer.toString(maxCrowNum)));
                                // после обрабтки ошибки в catch вышли из этого блока
                                if (editTextError) {
                                    editTextError = false;
                                    break;
                                }
                                Toast toast = Toast.makeText(getActivity(),
                                        "Новый максимум счёта ворон: " + maxCrowNum + " ворон", Toast.LENGTH_SHORT);
                                toast.show();

                            }
                            // в случае неправильного ввода лимита ворон обрабатываем исключение
                            catch (NumberFormatException e) {
                                editTextError = true; // присвоили переменной true так как ошибка произошла
                                // изменили записанные в файл настроек неправильный данные
                                // неправильные данные автоматически записываются в файл и этот момент нужно нам руками исправить
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(CROW_LIMIT, String.valueOf(maxCrowNum));
                                Toast toast = Toast.makeText(getActivity(),
                                        "Ошибка! Недопустимый формат ввода данных!", Toast.LENGTH_SHORT);
                                toast.show();
                                // сохранили изменения в файле настроек
                                editor.apply();
                            }
                            break;
                        }
                    }
                }
            };
        }

        @Override
        public void onResume() {
            super.onResume();
            // "привязали стукача"
            getPreferenceScreen()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        }

        @Override
        public void onPause() {
            super.onPause();
            // "отвязали стукача"
            getPreferenceScreen()
                    .getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        }
    }

}