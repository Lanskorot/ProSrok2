package com.example.prosrok;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class show_info extends AppCompatActivity {

    private TextView infoTextView;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);

        infoTextView = findViewById(R.id.textView6);

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
                String overdueInfo = getOverdueInfoYesterdayTo3(jsonArray);
                displayOverdueInfo(overdueInfo, R.color.orange);
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String overdueInfo = getOverdueInfo7To14(jsonArray);
                displayOverdueInfo(overdueInfo, R.color.green);
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

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Показать всплывающее окно при долгом нажатии
                int position = 0;  // Получите позицию элемента в зависимости от вашей логики
                selectedPosition = position; // Установите выбранную позицию
                showPopupMenu(v, position);
                return true;
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

        Date today = calendar.getTime();

        // Изменено: добавляем один день к текущей дате
        Calendar tomorrowCalendar = Calendar.getInstance();
        tomorrowCalendar.setTime(today);
        tomorrowCalendar.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = tomorrowCalendar.getTime();

        return getOverdueInfo(jsonArray, today, tomorrow);
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
        }

        // Сортировка списка по возрастанию даты
        Collections.sort(sortedList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    Date date1 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(o1.getString("дата окончания срока"));
                    Date date2 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(o2.getString("дата окончания срока"));
                    return date1.compareTo(date2); // Изменено на возрастающее сравнение
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        // Формирование строки вывода
        StringBuilder overdueInfo = new StringBuilder();
        for (JSONObject jsonObject : sortedList) {
            try {
                overdueInfo.append("Код товара: ").append(jsonObject.getString("штрих-код")).append("\n")
                        .append("Название: ").append(jsonObject.getString("название")).append("\n")
                        .append("Количество: ").append(jsonObject.getString("количество")).append("\n")
                        .append("Дата окончания срока: ").append(jsonObject.getString("дата окончания срока")).append("\n")
                        .append("Комментарий: ").append(jsonObject.getString("комментарий")).append("\n\n")
                        .append(generateSeparator()).append("\n\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return overdueInfo.toString();
    }

    private String getOverdueInfoBeforeToday(JSONArray jsonArray) {
        StringBuilder overdueInfo = new StringBuilder();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String dateString = jsonObject.getString("дата окончания срока");
                Date expirationDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(dateString);
                // Проверяем, просрочен ли товар и дата в прошлом относительно текущей даты
                if (expirationDate.before(getDateWithoutTime(new Date()))) {
                    overdueInfo.append("Код товара: ").append(jsonObject.getString("штрих-код")).append("\n")
                            .append("Название: ").append(jsonObject.getString("название")).append("\n")
                            .append("Количество: ").append(jsonObject.getString("количество")).append("\n")
                            .append("Дата окончания срока: ").append(jsonObject.getString("дата окончания срока")).append("\n")
                            .append("Комментарий: ").append(jsonObject.getString("комментарий")).append("\n\n")
                            .append(generateSeparator()).append("\n\n");
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }

        return overdueInfo.toString();
    }

    // Метод для получения даты без времени
    private Date getDateWithoutTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    private String getOverdueInfo7To14(JSONArray jsonArray) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date today = new Date();
        Date startDay = getDateAfterDays(3);  // Изменено: начало диапазона от +3 дня
        Date endDay = getDateAfterDays(7);    // Изменено: конец диапазона +7 дней

        List<JSONObject> sortedList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String dateString = jsonObject.getString("дата окончания срока");
                Date expirationDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(dateString);

                // Проверяем, просрочен ли товар и дата в диапазоне "завтра + 3 дня"
                if (isDateInRange(expirationDate, startDay, endDay)) {
                    sortedList.add(jsonObject);
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }

        // Сортировка списка по возрастанию даты
        Collections.sort(sortedList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    Date date1 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(o1.getString("дата окончания срока"));
                    Date date2 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(o2.getString("дата окончания срока"));
                    return date1.compareTo(date2); // Изменено на обратное сравнение
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        // Формирование строки вывода
        StringBuilder overdueInfo = new StringBuilder();
        for (JSONObject jsonObject : sortedList) {
            try {
                overdueInfo.append("Код товара: ").append(jsonObject.getString("штрих-код")).append("\n")
                        .append("Название: ").append(jsonObject.getString("название")).append("\n")
                        .append("Количество: ").append(jsonObject.getString("количество")).append("\n")
                        .append("Дата окончания срока: ").append(jsonObject.getString("дата окончания срока")).append("\n")
                        .append("Комментарий: ").append(jsonObject.getString("комментарий")).append("\n\n")
                        .append(generateSeparator()).append("\n\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return overdueInfo.toString();
    }

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

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String dateString = jsonObject.getString("дата окончания срока");
                Date expirationDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(dateString);

                if (isDateInRange(expirationDate, startDate, endDate)) {
                    overdueInfo.append("Код товара: ").append(jsonObject.getString("штрих-код")).append("\n")
                            .append("Название: ").append(jsonObject.getString("название")).append("\n")
                            .append("Количество: ").append(jsonObject.getString("количество")).append("\n")
                            .append("Дата окончания срока: ").append(jsonObject.getString("дата окончания срока")).append("\n")
                            .append("Комментарий: ").append(jsonObject.getString("комментарий")).append("\n\n")
                            .append(generateSeparator()).append("\n\n");
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }

        return overdueInfo.toString();
    }

    private String generateSeparator() {
        StringBuilder separator = new StringBuilder();
        for (int i = 0; i < 40; i++) {
            separator.append("-");
        }
        return separator.toString();
    }

    private boolean isDateInRange(Date date, Date startDate, Date endDate) {
        return date.after(startDate) && date.before(endDate);
    }

    private void displayOverdueInfo(final String overdueInfo, final int textColor) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                infoTextView.setText(overdueInfo);
                infoTextView.setTextColor(getResources().getColor(textColor));
            }
        });
    }

    private String formatData(String jsonArrayString) {
        StringBuilder formattedText = new StringBuilder();

        try {
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            jsonArray = sortJsonArrayByDate(jsonArray);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String formattedItem = "Код товара: " + jsonObject.getString("штрих-код") + "\n" +
                        "Наименование товара: " + jsonObject.getString("название") + "\n" +
                        "Количество: " + jsonObject.getString("количество") + "\n" +
                        "Последняя дата: " + jsonObject.getString("дата окончания срока") + "\n" +
                        "Комментарии: " + jsonObject.getString("комментарий") + "\n\n";

                formattedText.append(formattedItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return formattedText.toString();
    }

    private JSONArray sortJsonArrayByDate(JSONArray jsonArray) {
        List<JSONObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonValues.add(jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            private static final String DATE_KEY = "дата окончания срока";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                String dateStringA = "";
                String dateStringB = "";

                try {
                    dateStringA = a.getString(DATE_KEY);
                    dateStringB = b.getString(DATE_KEY);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                Date dateA, dateB;

                try {
                    dateA = sdf.parse(dateStringA);
                    dateB = sdf.parse(dateStringB);
                    return dateA.compareTo(dateB);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return 0; // Default to no sorting
            }
        });

        return new JSONArray(jsonValues);
    }

    public void go_to_main(View view) {
        // Ваш код для обработки нажатия кнопки и перехода к главному экрану
        Intent intent = new Intent(this, MainActivity.class); // Замените MainActivity на класс вашей главной активности
        startActivity(intent);
    }
    private int selectedPosition;
    private void showPopupMenu(View view, final int position) {
        Log.d("ShowPopupMenu", "Position in showPopupMenu: " + position);
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.context_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                selectedPosition = position;
                int itemId = item.getItemId();

                if (itemId == R.id.menu_delete) {
                    // Обработка удаления
                    Log.d("ShowPopupMenu", "Position in deleteItem: " + selectedPosition);
                    deleteItem();
                    return true;
                } else if (itemId == R.id.menu_edit) {
                    // Обработка редактирования с передачей позиции
                    Log.d("ShowPopupMenu", "Position in editItem: " + selectedPosition);
                    editItem();
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }


    private void editItem() {
        // Получите выбранный JSONObject с использованием выбранной позиции
        JSONObject selectedObject = null;
        try {
            selectedObject = jsonArray.getJSONObject(selectedPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Передайте данные в активити edit для редактирования, включая позицию
        Intent editIntent = new Intent(this, edit.class);
        editIntent.putExtra("selectedObject", selectedObject.toString());
        editIntent.putExtra("position", selectedPosition);
        startActivity(editIntent);
    }

    private void deleteItem() {
        // Логирование для проверки содержимого jsonArray перед удалением
        Log.d("DeleteItem", "jsonArray before deletion: " + jsonArray.toString());

        try {
            // Удаляем элемент из списка JSON с использованием выбранной позиции
            jsonArray.remove(selectedPosition);

            // Логирование для проверки содержимого jsonArray после удаления
            Log.d("DeleteItem", "jsonArray after deletion: " + jsonArray.toString());

            // Сохраняем обновленный список в вашем приложении
            // Например, можно использовать SharedPreferences или базу данных
            // В этом примере, предполагается, что у вас есть метод для сохранения jsonArray в SharedPreferences
            saveJsonArrayToSharedPreferences(jsonArray);

            // Обновляем интерфейс после удаления
            updateInterface();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DeleteItem", "Error deleting item at selected position: " + selectedPosition);
        }
    }

    private void saveJsonArrayToSharedPreferences(JSONArray jsonArray) {
        // Ваш код для сохранения jsonArray в SharedPreferences
        // Например, использование SharedPreferences для хранения строки JSON
    }

    private void updateInterface() {
        // Обновите ваш интерфейс после удаления элемента
        // Например, вызовите метод для отображения данных в textView6
        String formattedData = formatData(jsonArray.toString());
        infoTextView.setText(formattedData);
        // Устанавливаем цвет текста в черный
        infoTextView.setTextColor(getResources().getColor(android.R.color.black));
    }
}

