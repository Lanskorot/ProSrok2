package com.example.prosrok;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_and_deleting);

        listView = findViewById(R.id.listView);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        closeButton = findViewById(R.id.closeButton);

        jsonArray = loadDataFromPreferences();
        dataList = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                dataList.add(jsonArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getFormattedList());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Обработка выбора элемента списка
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

    private JSONArray loadDataFromPreferences() {
        // Ваш код загрузки данных
        return new JSONArray();
    }

    private ArrayList<String> getFormattedList() {
        ArrayList<String> formattedList = new ArrayList<>();
        for (JSONObject element : dataList) {
            try {
                formattedList.add(element.getString("штрих-код") + " - " +
                        element.getString("дата окончания срока") + " - " +
                        element.getString("название"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return formattedList;
    }

    private void editEntry() {
        int selectedPosition = listView.getSelectedItemPosition();

        if (selectedPosition != AdapterView.INVALID_POSITION) {
            JSONObject selectedObject = dataList.get(selectedPosition);

            // Реализуйте функциональность редактирования данных
            // Подобно тому, как это сделано в Python-коде
        }
    }

    private void deleteEntry() {
        int selectedPosition = listView.getSelectedItemPosition();

        if (selectedPosition != AdapterView.INVALID_POSITION) {
            dataList.remove(selectedPosition);

            // Реализуйте функциональность удаления данных
            // Подобно тому, как это сделано в Python-коде

            adapter.clear();
            adapter.addAll(getFormattedList());
            adapter.notifyDataSetChanged();
        }
    }
}