package com.example.prosrok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

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
        Button button8 = findViewById(R.id.button8);
        Button button9 = findViewById(R.id.button9);

        // Получаем переданные данные из Intent
        String jsonArrayString = getIntent().getStringExtra("jsonArray");

        try {
            jsonArray = new JSONArray(jsonArrayString);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonArray = new JSONArray();
        }

        // Отображаем данные в TextView с форматированием
        String formattedData = formatData(jsonArrayString);
        TextView textView = findViewById(R.id.textView6);
        textView.setText(formattedData);
        String overdueInfo = getOverdueInfoBeforeToday(jsonArray);
        displayOverdueInfo(overdueInfo, R.color.red);





        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String overdueInfo = getOverdueInfoBeforeToday(jsonArray);
                displayOverdueInfo(overdueInfo, R.color.red);
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
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


        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Вызываем метод formatData для отображения данных в textView6
                String formattedData = formatData(jsonArrayString);
                jsonArray = sortJsonArrayByDate(jsonArray);
                infoTextView.setText(formattedData);

                // Устанавливаем цвет текста в черный
                infoTextView.setTextColor(getResources().getColor(android.R.color.black));
            }
        });
    }

    private String getAdditionalInfo() {
        // Измените этот метод, чтобы возвращать необходимую информацию в соответствии с вашей логикой
        return "Дополнительная информация для отображения";
    }

    public void onButton6Click(View view) {
        String overdueInfo = getOverdueInfoToday(jsonArray);
        displayOverdueInfo(overdueInfo, R.color.red);
    }

    public void onButton7Click(View view) {
        String overdueInfo = getOverdueInfoYesterdayTo3(jsonArray);
        displayOverdueInfo(overdueInfo, R.color.orange);
    }

    public void onButton8Click(View view) {
        String overdueInfo = getOverdueInfo3To7(jsonArray);
        displayOverdueInfo(overdueInfo, R.color.green);
    }

    private String getOverdueInfoToday(JSONArray jsonArray) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

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



    private String getOverdueInfoYesterdayTo3(JSONArray jsonArray) {
        List<JSONObject> sortedList = new ArrayList<>();

        // Изменение: Получаем вчерашнюю дату
        Calendar yesterdayCalendar = Calendar.getInstance();
        yesterdayCalendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = yesterdayCalendar.getTime();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String dateString = jsonObject.getString("дата окончания срока");
                Date expirationDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(dateString);

                // Проверяем, просрочен ли товар и дата в диапазоне "вчера + 3 дня"
                if (isDateInRange(expirationDate, getDateWithoutTime(yesterday), getDateAfterDays(3))) {
                    sortedList.add(jsonObject);
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
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


    private Date getDateAfterDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    private String getOverdueInfo3To7(JSONArray jsonArray) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date today = new Date();
        Date startDay = new Date(today.getTime() + (3 * 24 * 60 * 60 * 1000));
        Date endDay = new Date(today.getTime() + (7 * 24 * 60 * 60 * 1000));
        return getOverdueInfo(jsonArray, startDay, endDay);
    }

    public void goToMain(View view) {
        // ваш код для перехода на главный экран
        Intent intent = new Intent(this, MainActivity.class); // Замените YourMainActivity.class на класс вашей главной активности
        startActivity(intent);
    }

    private String getOverdueInfo(JSONArray jsonArray, Date startDate, Date endDate) {
        StringBuilder overdueInfo = new StringBuilder();

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