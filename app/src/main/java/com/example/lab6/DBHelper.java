package com.example.lab6;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.database.Cursor;
import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "products.db";
    private static String DB_LOCATION;
    private static final int DB_VERSION = 1;

    private final Context myContext;
    private SQLiteDatabase database;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
        DB_LOCATION = context.getApplicationInfo().dataDir + "/databases/";

        // Проверяем и копируем БД при создании класса
        if (!checkDB()) {
            copyDB();
        }
        openDB();
    }


    // Проверяем существует ли БД
    private boolean checkDB() {
        File fileDB = new File(DB_LOCATION + DB_NAME);
        return fileDB.exists();
    }

    // Копируем БД из assets во внутреннюю память
    private void copyDB() {
        try {
            // Создаем папку databases если её нет
            File directory = new File(DB_LOCATION);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Копируем файл
            InputStream inputStream = myContext.getAssets().open(DB_NAME);
            OutputStream outputStream = new FileOutputStream(DB_LOCATION + DB_NAME);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Открываем БД
    private void openDB() {
        database = SQLiteDatabase.openDatabase(DB_LOCATION + DB_NAME,
                null, SQLiteDatabase.OPEN_READWRITE);
    }

    // Закрываем БД
    public void closeDB() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    public ArrayList<HashMap<String, String>> getCategories() {
        ArrayList<HashMap<String, String>> categories = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM categories", null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> category = new HashMap<>();
                category.put("id", cursor.getString(0));
                category.put("name", cursor.getString(1));
                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    // Получить товары по категории
    public ArrayList<HashMap<String, String>> getProductsByCategory(int categoryId) {
        ArrayList<HashMap<String, String>> products = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM products WHERE category_id = ?",
                new String[]{String.valueOf(categoryId)}
        );

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> product = new HashMap<>();
                product.put("name", cursor.getString(1));
                product.put("description", cursor.getString(2));
                product.put("price", cursor.getString(3));
                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void updateDB() {
        if (database != null && database.isOpen()) {
            database.close();
        }

        // Удаляем старую БД
        File fileDB = new File(DB_LOCATION + DB_NAME);
        if (fileDB.exists()) {
            fileDB.delete();
        }

        // Копируем новую БД из assets
        copyDB();
        openDB();
    }
}