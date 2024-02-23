package com.example.prosrok;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.CaptureActivity;

public class ScanActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private Button btn_scan;

    // Добавляем DatabaseHelper
    private DataBaseAssetsHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Инициализируем DatabaseHelper
        dbHelper = new DataBaseAssetsHelper(this);

        // Вызываем метод для проверки разрешения и запуска сканера
        checkCameraPermissionAndStartScanner();

        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermissionAndStartScanner();
            }
        });
    }

    private void checkCameraPermissionAndStartScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Разрешение уже предоставлено, выполняйте ваш код сканирования штрих-кода
            startBarcodeScanner();
        } else {
            // Разрешение не предоставлено, запросите его у пользователя
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено, выполняйте ваш код сканирования штрих-кода
                startBarcodeScanner();
            } else {
                // Разрешение не предоставлено, обработайте ситуацию, например, выведите сообщение пользователю
                Toast.makeText(this, "Разрешение на использование камеры не предоставлено", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startBarcodeScanner() {
        Intent intent = new Intent(this, CaptureActivity.class);
        intent.setAction("com.google.zxing.client.android.SCAN");
        intent.putExtra("SAVE_HISTORY", false);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");

        // Запускаем сканер с использованием ActivityResultLauncher
        scanLauncher.launch(intent);
    }

// Создаем ActivityResultLauncher для обработки результатов сканирования
private final ActivityResultLauncher<Intent> scanLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK) {
                // Обработка результатов сканирования
                Intent data = result.getData();
                if (data != null) {
                    String barcode = data.getStringExtra("SCAN_RESULT");
                    // Запрашиваем данные из базы данных
                    String resultFromDatabase = dbHelper.getDataFromBarcodeEAN(barcode);
                    if (resultFromDatabase != null) {
                        // Делаем что-то с данными из базы данных
                        Toast.makeText(this, "Данные из базы данных: " + resultFromDatabase, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Данные для штрих-кода " + barcode + " не найдены", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (result.getResultCode() == RESULT_CANCELED) {
                Toast.makeText(this, "Сканирование отменено", Toast.LENGTH_SHORT).show();
            }
        });

    protected void onDestroy() {
        super.onDestroy();
        // Закрываем dbHelper при уничтожении активности
        dbHelper.close();
    }
}