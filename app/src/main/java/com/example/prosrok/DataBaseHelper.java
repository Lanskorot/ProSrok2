// package com.example.prosrok;

//public class DatabaseHelper extends SQLiteOpenHelper {
//
//    public static final String TAG = "DatabaseHelperTag";
//
//    public static final String DATABASE_NAME = "bar_code_base.sqlite";  // table name is 'total_data'
//    public static final int DATABASE_VERSION = 3;
//
//    public final Context context;
//
//    // Конструктор
//    public DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        this.context = context;
//    }

    //public String getAllData() {
    //    SQLiteDatabase db = this.getReadableDatabase();
    //    String query = "SELECT * FROM android_metadata";
    //    Cursor cursor = db.rawQuery(query, null);
//
    //    StringBuilder result = new StringBuilder();
//
    //    if (cursor != null && cursor.moveToFirst()) {
    //        do {
    //            for (int i = 0; i < cursor.getColumnCount(); i++) {
    //                String columnName = cursor.getColumnName(i);
    //                int columnIndex = cursor.getColumnIndex(columnName);
    //                String value = cursor.getString(columnIndex);
//
    //                result.append(columnName).append(": ").append(value).append("\n");
    //            }
    //            result.append("\n");
    //        } while (cursor.moveToNext());
    //    }
//
    //    if (cursor != null) {
    //        cursor.close();
    //    }
//
    //    db.close();
    //    return result.toString();
    //}


    //@Override
    //public void onCreate(SQLiteDatabase db) {
    //    // Копируем файл базы данных из assets при создании
    //    // Check if the database file exists
    //    File dbFile = context.getDatabasePath(DATABASE_NAME);
    //    if (dbFile.exists()) {
    //        Log.d("onCreate method by DatabaseHelper", "DB exists");
    //    }
    //    if (!dbFile.exists()) {
    //        Log.d("onCreate method by DatabaseHelper", "DB was NOT exists");
    //        // Database file does not exist, perform initialization
    //        // For example, you can create tables here
    //        db.execSQL("CREATE TABLE IF NOT EXISTS total_data ("
    //                + "Preisliste TEXT,"
    //                + "Preislistenverweise REAL,"
    //                + "Preislisteninformationen TEXT,"
    //                + "Artikelnummer TEXT,"
    //                + "Artikelnummer2 TEXT,"
    //                + "\"EAN Nummer\" TEXT,"
    //                + "Beschreibung TEXT,"
    //                + "Menge INTEGER,"
    //                + "Einheit TEXT,"
    //                + "\"Berücksichtigung VPME\" TEXT,"
    //                + "Einzelpreis REAL,"
    //                + "Wkz TEXT"
    //                + ")");
    //    }

//        try {
//            context.getAssets().open(DATABASE_NAME);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    //  }

//   @Override
//   public void onCreate(SQLiteDatabase db) {

//   }

//   @Override
//   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//       // Если нужно обновить схему базы данных, добавьте соответствующий код здесь
//   }
    //    private void copyDatabaseFromAssets() throws IOException {
//        try {
//            InputStream inputStream = context.getAssets().open(DATABASE_NAME);
//            String outFileName = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
//            OutputStream outputStream = new FileOutputStream(outFileName);
//
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = inputStream.read(buffer)) > 0) {
//                outputStream.write(buffer, 0, length);
//            }
//
//            // Закрываем потоки
//            outputStream.flush();
//            outputStream.close();
//            inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public String getDataFromBarcode(String barcode) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT * FROM total_data WHERE Artikelnummer = ?";
//        Cursor cursor = db.rawQuery(query, new String[]{barcode});
//
//        String result = null;
//
//        if (cursor != null && cursor.moveToFirst()) {
//            int columnIndex = cursor.getColumnIndex("Beschreibung");
//            result = cursor.getString(columnIndex);
//        }
//
//        if (cursor != null) {
//            cursor.close();
//        }
//
//        if (result == null) {
//            return "Товар не найден.";
//        }
//
//        db.close();
//        return result;
//    }
//}