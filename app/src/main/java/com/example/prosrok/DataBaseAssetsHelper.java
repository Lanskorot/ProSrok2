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
        String query = "SELECT Beschreibung, `Artikelnummer` FROM total_data WHERE `EAN Nummer` = ?";
        Cursor cursor = db.rawQuery(query, new String[]{barcode});

        String resultBeschreibung = null;
        String resultArtikelnummer = null;

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndexBeschreibung = cursor.getColumnIndex("Beschreibung");
            resultBeschreibung = cursor.getString(columnIndexBeschreibung);

            int columnIndexArtikelnummer = cursor.getColumnIndex("Artikelnummer");
            resultArtikelnummer = cursor.getString(columnIndexArtikelnummer);
        }

        db.close();

        if (cursor != null) {
            cursor.close();
        }

        if (resultBeschreibung == null) {
            resultBeschreibung = "Товар не найден.";
        }

        if (resultArtikelnummer == null) {
            resultArtikelnummer = "Товар не найден.";
        }

        // You can return both values in a more structured way, e.g., as a JSON string or a custom object
        return resultArtikelnummer + "," + resultBeschreibung;

    }

}
