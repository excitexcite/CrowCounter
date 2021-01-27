package com.peapod.crowcounter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class NotesActivity extends AppCompatActivity {

    private final static String FILENAME = "notes.txt"; // имя файла, который будет содержать заметки
    private EditText editText; // переменна для управления текстовым полем с макета
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        editText = findViewById(R.id.editText); // связали переменную с макетом с помозью id
        File file = new File(getApplicationContext().getFilesDir(), FILENAME); // объект для проверки существования
        if (file.exists()) {
            readFile(FILENAME); // если файл сущетвует, то читаем заметку
        }
    }

    // функция для отображения меню (иконок)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes, menu); // берем данные из ресурсов меню и преобразовываем их в меню на макете
        return true;
    }


    // функция для обработки нажатий на элементы меню
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.save: {
                writeFile(FILENAME);
                Toast.makeText(getApplicationContext(), "Заметка успешно сохранена", Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
            case R.id.copy: {
                // создали объект управления буфером обмена
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // получили данные из поля ввода
                ClipData clipData = ClipData.newPlainText("Note text", editText.getText());
                clipboard.setPrimaryClip(clipData); // поместили данные буфер обмена
                Toast.makeText(getApplicationContext(), "Текст скопирован", Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
            case R.id.select: {
                String str = editText.getText().toString();
                // если заметка не пустая (есть что выделить)
                if (!str.isEmpty()) {
                    // добавили возможность выделения текста
                    editText.setSelectAllOnFocus(true);
                    editText.clearFocus(); // очистили фокус перед выделением
                    editText.requestFocus(); // выделили весь такст
                    // объект для управления метода ввода данных (кливиатурой)
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT); // открыли клавиатуру
                    Toast.makeText(getApplicationContext(), "Текст выделен", Toast.LENGTH_SHORT)
                            .show();
                }
                return true;
            }
            case R.id.delete: {
                editText.setText("");
                writeFile(FILENAME);
                Toast.makeText(getApplicationContext(), "Текст удален", Toast.LENGTH_SHORT)
                        .show();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // чтение заметки из файла
    private void readFile(String fname) {
        try (FileInputStream fis = getApplicationContext().openFileInput(fname)) {
            // если файл существует и открылся
            if (fis != null) {
                // объекты для чтения из файла
                InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                StringBuilder builder = new StringBuilder();
                // читаем файл построчно
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                // выводим заметку на экран
                editText.setText(builder.toString());
            }
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(), "Ошибка при чтении заметки!", Toast.LENGTH_LONG).show();
            t.printStackTrace();
        }
    }

    // сохранение заметки в файл
    private void writeFile(String fname) {
        // если файл открылся, то пишем в него заметку
        try (FileOutputStream fos = getApplicationContext().openFileOutput(fname, Context.MODE_PRIVATE) ){
            fos.write(editText.getText().toString().getBytes());
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(), "Ошибка при записи в файл", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
