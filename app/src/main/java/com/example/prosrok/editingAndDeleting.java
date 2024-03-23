package com.example.prosrok;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class editingAndDeleting extends AppCompatActivity {

    private ListView listView;
    private Button editButton, deleteButton, closeButton;
    private JSONArray jsonArray;
    private ArrayList<JSONObject> dataList;
    private ArrayAdapter<String> adapter;
    private int selectedPosition = AdapterView.INVALID_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_and_deleting);

        listView = findViewById(R.id.listView);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        closeButton = findViewById(R.id.closeButton);

        // Получение данных из Intent
        Intent intent = getIntent();
        if (intent != null) {
            String jsonArrayString = intent.getStringExtra("jsonArray");
            Log.d("MyTag", "Received jsonArrayString: " + jsonArrayString);

            try {
                jsonArray = new JSONArray(jsonArrayString);
                dataList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    dataList.add(jsonArray.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Проверка, пуст ли исходный массив данных
        if (jsonArray == null || jsonArray.length() == 0) {
            // Если массив пуст, отобразить сообщение "файл пустой"
            ArrayList<String> emptyList = new ArrayList<>();
            emptyList.add("Файл пустой");
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emptyList);
            listView.setAdapter(adapter);
        } else {
            // Если массив не пуст, отобразить данные из списка
            loadDataFromPreferences();
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getFormattedList());
            listView.setAdapter(adapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedPosition == position) {
                    // Если выбранный элемент снова нажат, снимаем выделение
                    listView.getChildAt(position - listView.getFirstVisiblePosition()).setBackgroundColor(Color.TRANSPARENT);
                    selectedPosition = AdapterView.INVALID_POSITION;
                } else {
                    // Снимаем выделение с предыдущего выбранного элемента
                    if (selectedPosition != AdapterView.INVALID_POSITION) {
                        listView.getChildAt(selectedPosition - listView.getFirstVisiblePosition()).setBackgroundColor(Color.TRANSPARENT);
                    }

                    // Обрабатываем выбор элемента списка
                    selectedPosition = position;

                    // Подсвечиваем текущий выбранный элемент
                    listView.getChildAt(position - listView.getFirstVisiblePosition()).setBackgroundColor(getResources().getColor(R.color.main));
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEntry();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEntry();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateListAndAdapter() {
        // Удаление элемента из dataList
        if (selectedPosition != AdapterView.INVALID_POSITION) {
            dataList.remove(selectedPosition);
        }

        // Обновление адаптера и уведомление об изменении данных
        adapter.clear();
        adapter.addAll(getFormattedList());
        adapter.notifyDataSetChanged();

        // Сброс выбранной позиции
        selectedPosition = AdapterView.INVALID_POSITION;

        Log.d("MyTag", "Adapter updated");
    }

    private ArrayList<String> getFormattedList() {
        ArrayList<String> formattedList = new ArrayList<>();
        for (JSONObject element : dataList) {
            try {
                String barcode = element.getString("штрих-код");
                String expirationDate = element.has("дата окончания срока") ? element.getString("дата окончания срока") : "N/A";
                String name = element.getString("название");
                formattedList.add(barcode + " - " + expirationDate + " - " + name);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("MyTag", "Error while formatting list: " + e.getMessage());
            }
        }

        Log.d("MyTag", "Formatted list: " + formattedList);
        return formattedList;
    }

    private void editEntry() {
        if (selectedPosition != AdapterView.INVALID_POSITION) {
            // Реализуйте функциональность редактирования данных
            // Подобно тому, как это сделано в Python-коде
            updateListAndAdapter();
        }
    }


    private void deleteEntry() {
        if (selectedPosition != AdapterView.INVALID_POSITION) {
            Log.d("MyTag", "Deleting entry at position: " + selectedPosition);

            // Удаление элемента из jsonArray
            JSONArray updatedJsonArray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                if (i != selectedPosition) {
                    try {
                        updatedJsonArray.put(jsonArray.get(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("MyTag", "Error while updating jsonArray: " + e.getMessage());
                    }
                }
            }

            // Обновляем jsonArray
            jsonArray = updatedJsonArray;

            // Пересоздание dataList на основе обновленного jsonArray
            dataList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    dataList.add(jsonArray.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("MyTag", "Error while creating dataList: " + e.getMessage());
                }
            }

            // Обновление адаптера и уведомление об изменении данных
            adapter.clear();
            adapter.addAll(getFormattedList());
            adapter.notifyDataSetChanged();

            // Сброс выбранной позиции
            selectedPosition = AdapterView.INVALID_POSITION;

            // Сохранение обновленного jsonArray в SharedPreferences
            Log.d("MyTag", "Before saving to SharedPreferences: " + jsonArray.toString());
            saveDataToPreferences(jsonArray.toString());
            Log.d("MyTag", "After saving to SharedPreferences: " + jsonArray.toString());

            Log.d("MyTag", "Item removed from jsonArray");
            // Создайте новый Intent
            Intent updateIntent = new Intent();
                // Поместите обновленный jsonArray в Intent
            updateIntent.putExtra("updatedJsonArray", jsonArray.toString());
            // Установите результат с кодом RESULT_OK
            setResult(RESULT_OK, updateIntent);
            // Завершите текущую активность
            finish();
        }
    }

    private void saveDataToPreferences(String jsonArrayString) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("jsonArray", jsonArrayString);
        editor.apply();
        Log.d("MyTag", "Saved jsonArray to SharedPreferences: " + jsonArrayString);
    }

    private void loadDataFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String jsonArrayString = preferences.getString("jsonArray", "");
        try {
            jsonArray = new JSONArray(jsonArrayString);
            dataList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                dataList.add(jsonArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveDataToPreferences(jsonArray.toString());
    }
}