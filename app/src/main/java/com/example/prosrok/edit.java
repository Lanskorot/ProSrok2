package com.example.prosrok;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class edit extends AppCompatActivity {
    private EditText editTextText6, editTextText2, editTextText3, editTextText4, editTextText5;
    private JSONArray jsonArray;
    private Button btn_scan;



    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        sdf.setLenient(false); // Устанавливаем строгий режим парсинга

        try {
            // Пытаемся распарсить введенную дату
            sdf.parse(date);
            return true; // Если успешно, дата корректна
        } catch (Exception e) {
            return false; // Если произошла ошибка парсинга, дата некорректна
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        editTextText6 = findViewById(R.id.editTextText6);
        editTextText2 = findViewById(R.id.editTextText2);
        editTextText3 = findViewById(R.id.editTextText3);
        editTextText4 = findViewById(R.id.editTextText4);
        editTextText5 = findViewById(R.id.editTextText5);
        Button button = findViewById(R.id.button);

        Intent intent = getIntent();
        if (intent != null) {
            String selectedObjectString = intent.getStringExtra("selectedObject");
            int position = intent.getIntExtra("position", -1);

            if (selectedObjectString != null && position != -1) {
                try {
                    // Получите выбранный JSONObject и заполните соответствующие поля
                    JSONObject selectedObject = new JSONObject(selectedObjectString);
                    editTextText6.setText(selectedObject.getString("штрих-код"));
                    editTextText2.setText(selectedObject.getString("название"));
                    editTextText3.setText(selectedObject.getString("количество"));
                    editTextText4.setText(selectedObject.getString("дата окончания срока"));
                    editTextText5.setText(selectedObject.getString("комментарий"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        editTextText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 2 && editable.charAt(1) != '.') {
                    editable.insert(2, ".");
                } else if (editable.length() == 5 && editable.charAt(4) != '.') {
                    editable.insert(5, ".");
                } else if (editable.length() > 10) {
                    editable.replace(0, 10, editable.subSequence(0, 10));
                }
            }
        });jsonArray = loadDataFromPreferences();




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredDate = editTextText4.getText().toString().trim();

                // Проверка корректности формата даты
                if (isValidDate(enteredDate)) {

                    addDataToArray();
                    saveDataToPreferences(jsonArray.toString());
                    removeExpiredItems();
                    editTextText6.post(new Runnable() {
                        @Override
                        public void run() {
                            editTextText6.setSelection(0);
                            editTextText6.requestFocus();
                        }
                    });

                    finish();
                } else {
                    // Вывести сообщение об ошибке
                    Toast.makeText(edit.this, "Некорректная дата", Toast.LENGTH_SHORT).show();
                    // Очистить поле ввода
                    editTextText4.setText("");
                }
            }
        });


    }
    private void removeExpiredItems() {
        JSONArray updatedArray = new JSONArray();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject item = jsonArray.getJSONObject(i);

                // Получаем дату из строки в формате "dd.MM.yyyy"
                String dateString = item.getString("дата окончания срока");
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                Date expirationDate = sdf.parse(dateString);

                // Проверяем, истекла ли дата
                if (!isExpired(expirationDate)) {
                    updatedArray.put(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Произошла ошибка при удалении истекших элементов", Toast.LENGTH_SHORT).show();
            }
        }

        // Заменяем оригинальный массив обновленным массивом без истекших элементов
        jsonArray = updatedArray;

        // Сохраняем обновленный массив в SharedPreferences
        saveDataToPreferences(jsonArray.toString());
    }

    private boolean isExpired(Date expirationDate) {
        if (expirationDate != null) {
            Date today = new Date();
            long twoDaysInMillis = 2 * 24 * 60 * 60 * 1000; // 2 days in milliseconds
            Date expirationWithBuffer = new Date(expirationDate.getTime() + twoDaysInMillis);
            return expirationWithBuffer.before(today);
        }
        return false;
    }

    private JSONArray loadDataFromPreferences() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String jsonArrayString = preferences.getString("jsonArray", "[]");
        try {
            return new JSONArray(jsonArrayString);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private void saveDataToPreferences(String jsonArrayString) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("jsonArray", jsonArrayString);
        editor.apply();
    }

    public void show_data(View v) {
        Intent intent = new Intent(this, show_info.class);
        intent.putExtra("jsonArray", jsonArray.toString());
        startActivity(intent);
    }


    public void show_scan(View v) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }



    private void addDataToArray() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("штрих-код", editTextText6.getText().toString());
            jsonObject.put("название", editTextText2.getText().toString());
            jsonObject.put("количество", editTextText3.getText().toString());
            jsonObject.put("дата окончания срока", editTextText4.getText().toString());
            jsonObject.put("комментарий", editTextText5.getText().toString());

            jsonArray.put(jsonObject);
            editTextText6.setText("");
            editTextText2.setText("");
            editTextText3.setText("");
            editTextText4.setText("");
            editTextText5.setText("");

            Log.d("JSON Array", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Произошла ошибка при добавлении данных", Toast.LENGTH_SHORT).show();
        }

    }

    private void writeJsonToFile(String fileName, String json) {
        try {
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
            Toast.makeText(this, "Данные сохранены в " + fileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка сохранения данных", Toast.LENGTH_SHORT).show();
        }
    }
}

