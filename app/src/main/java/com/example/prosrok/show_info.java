package com.example.prosrok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class show_info extends Activity {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<Pair<Date, DataModel>> listData;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);

        init();
        db = FirebaseFirestore.getInstance();
        Button button3 = findViewById(R.id.button3);
        Button button9 = findViewById(R.id.button9);
        Button button6 = findViewById(R.id.button6);
        Button button7 = findViewById(R.id.button7);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создаем намерение для перехода в MainActivity
                Intent intent = new Intent(show_info.this, MainActivity.class);
                // Запускаем активити MainActivity
                startActivity(intent);
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Вызываем метод для получения данных и обновления списка
                fetchDataAndUpdateList();
            }
        });

        fetchDataAndUpdateList();


        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем сегодняшнюю дату
                Date currentDate = new Date();
                // Получаем дату через два дня
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 2);
                Date twoDaysLaterDate = calendar.getTime();

                // Фильтруем список данных по датам
                List<Pair<Date, DataModel>> filteredListData = new ArrayList<>();
                for (Pair<Date, DataModel> pair : listData) {
                    Date expirationDate = pair.first;
                    if (expirationDate.after(currentDate) && expirationDate.before(twoDaysLaterDate)) {
                        filteredListData.add(pair);
                    }
                }

                // Очищаем адаптер и выводим отфильтрованные данные
                adapter.clear();
                for (Pair<Date, DataModel> pair : filteredListData) {
                    DataModel dataModel = pair.second;
                    String item = "Код товара: " + dataModel.getBarcode() + "\n"
                            + "Название: " + dataModel.getItem_name() + "\n"
                            + "Количество: " + dataModel.getItem_quantity() + "\n"
                            + "Дата окончания срока: " + dataModel.getExpiration_date() + "\n"
                            + "Комментарий: " + dataModel.getComment_text()+ "\n"
                            + " ";

                    adapter.add(item);
                }

            }
        });

        db.collection("tivat")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String barcode = documentSnapshot.getString("barcode");
                            String itemName = documentSnapshot.getString("item_name");
                            String quantity = documentSnapshot.getString("item_quantity");
                            String expirationDate = documentSnapshot.getString("expiration_date");
                            String idNumber = documentSnapshot.getString("id_number");
                            String commentText = documentSnapshot.getString("comment_text");

                            try {
                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                                Date expirationDateObj = format.parse(expirationDate);

                                DataModel dataModel = new DataModel(idNumber, barcode, itemName, quantity, expirationDate, commentText);

                                listData.add(new Pair<>(expirationDateObj, dataModel));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        Collections.sort(listData, new Comparator<Pair<Date, DataModel>>() {
                            @Override
                            public int compare(Pair<Date, DataModel> o1, Pair<Date, DataModel> o2) {
                                return o1.first.compareTo(o2.first);
                            }
                        });

                        adapter.clear();

                        for (Pair<Date, DataModel> pair : listData) {
                            DataModel dataModel = pair.second;
                            String item = "Код товара: " + dataModel.getBarcode() + "\n"
                                    + "Название: " + dataModel.getItem_name() + "\n"
                                    + "Количество: " + dataModel.getItem_quantity() + "\n"
                                    + "Дата окончания срока: " + dataModel.getExpiration_date() + "\n"
                                    + "Комментарий: " + dataModel.getComment_text()+ "\n"
                                    + " ";

                            adapter.add(item);
                        }
                    }
                });
    }

    private void fetchDataAndUpdateList() {
        db.collection("tivat")

                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Ваш существующий код обработки данных и обновления списка...
                    }
                });
    }
    private void init() {
        listView = findViewById(R.id.textView6);
        listData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
    }
}