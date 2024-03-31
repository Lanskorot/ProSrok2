package com.example.prosrok;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BarAndArticle extends AppCompatActivity {

    private static final int SCAN_ACTIVITY_REQUEST_CODE = 1;

    private EditText[] editTextArray;
    private CheckBox[] checkBoxArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_and_article);

        editTextArray = new EditText[]{
                findViewById(R.id.editText),
                findViewById(R.id.editText2),
                findViewById(R.id.editText3),
                findViewById(R.id.editText4),
                findViewById(R.id.editText5),
                findViewById(R.id.editText6),
                findViewById(R.id.editText7),
                findViewById(R.id.editText8),
                findViewById(R.id.editText9),
                findViewById(R.id.editText10),
                findViewById(R.id.editText11),
                findViewById(R.id.editText12),
                findViewById(R.id.editText13),
                findViewById(R.id.editText14),
                findViewById(R.id.editText15),
                // Добавьте остальные EditText
        };

        checkBoxArray = new CheckBox[]{
                findViewById(R.id.checkBox),
                findViewById(R.id.checkBox2),
                findViewById(R.id.checkBox31),
                findViewById(R.id.checkBox32),
                findViewById(R.id.checkBox41),
                findViewById(R.id.checkBox42),
                findViewById(R.id.checkBox51),
                findViewById(R.id.checkBox52),
                findViewById(R.id.checkBox61),
                findViewById(R.id.checkBox62),
                findViewById(R.id.checkBox71),
                findViewById(R.id.checkBox72),
                findViewById(R.id.checkBox81),
                findViewById(R.id.checkBox82),
                findViewById(R.id.checkBox91),
                findViewById(R.id.checkBox92),
                findViewById(R.id.checkBox101),
                findViewById(R.id.checkBox102),
                findViewById(R.id.checkBox111),
                findViewById(R.id.checkBox112),
                findViewById(R.id.checkBox121),
                findViewById(R.id.checkBox122),
                findViewById(R.id.checkBox131),
                findViewById(R.id.checkBox132),
                findViewById(R.id.checkBox141),
                findViewById(R.id.checkBox142),
                findViewById(R.id.checkBox151),
                findViewById(R.id.checkBox152),
                findViewById(R.id.checkBox3),
                findViewById(R.id.checkBox4),
                // Добавьте остальные CheckBox
        };

        Button scanButton = findViewById(R.id.btn_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScanActivity();
            }
        });

        Button cleanButton = findViewById(R.id.Clean);
        cleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Очищаем все EditText
                for (EditText editText : editTextArray) {
                    editText.setText("");
                }
                // Снимаем все галочки с CheckBox
                for (CheckBox checkBox : checkBoxArray) {
                    checkBox.setChecked(false);
                }
            }
        });


        Button goToMainButton = findViewById(R.id.GoToMain);
        goToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });



    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Optional, if you want to close current activity upon going back to MainActivity
    }

    private void startScanActivity() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, SCAN_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.hasExtra("Beschreibung")) {
                    String[] beschreibung = data.getStringArrayExtra("Beschreibung");

                    // Разделим значения resultBeschreibung и resultArtikelnummer
                    assert beschreibung != null;
                    if (beschreibung.length >= 2) {
                        String resultArtikelnummer = beschreibung[0];
                        String resultBeschreibung = beschreibung[1];

                        // Найти первый пустой EditText для записи данных
                        for (int i = 0; i < editTextArray.length; i++) {
                            if (editTextArray[i].getText().toString().isEmpty()) {
                                editTextArray[i].setText(resultArtikelnummer);
                                break; // Прерываем цикл после записи данных
                            }
                        }
                    }
                }
            }
        }
    }

}