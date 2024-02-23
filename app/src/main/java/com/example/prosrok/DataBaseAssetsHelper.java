package com.example.prosrok;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DataBaseAssetsHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "bar_code_base.sqlite";  // table is 'total_data'
    private static final int DATABASE_VERSION = 3;

    public DataBaseAssetsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade(); // Force upgrade the database if needed
    }

    public String getDataFromBarcode(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT Beschreibung FROM total_data WHERE Artikelnummer = ?";
        Cursor cursor = db.rawQuery(query, new String[]{barcode});

        String result = null;

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("Beschreibung"); // описание
            result = cursor.getString(columnIndex);
        }

        db.close();

        if (cursor != null) {
            cursor.close();
        }

        if (result == null) {
            result = "Товар не найден.";
        }
        return result;
    }

    public String getDataFromBarcodeEAN(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT Beschreibung FROM total_data WHERE `EAN Nummer` = ?";
        Cursor cursor = db.rawQuery(query, new String[]{barcode});

        String result = null;

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("Beschreibung");
            result = cursor.getString(columnIndex);
        }

        db.close();

        if (cursor != null) {
            cursor.close();
        }

        if (result == null) {
            result = "Товар не найден.";
        }
        return result;
    }
}
