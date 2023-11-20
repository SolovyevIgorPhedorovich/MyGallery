package com.example.mygallery.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.constructors.CartConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCart extends DatabaseManager {
    private static String CURRENT_PATH;

    public DatabaseCart(Context context) {
        super(context);
        CURRENT_PATH = context.getApplicationInfo().dataDir + "/Корзина";
    }

    public List<Model> getCartFile() {
        List<Model> pathFavorites = new ArrayList<>();
        try {
            if (openOrInitializeDatabase()) {
                Cursor cursor = mDataBase.rawQuery("SELECT * FROM cart", null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                        File current_path = new File(cursor.getString(cursor.getColumnIndexOrThrow("current_path")));
                        File initial_path = new File(cursor.getString(cursor.getColumnIndexOrThrow("initial_path")));
                        int deletion_date = cursor.getInt(cursor.getColumnIndexOrThrow("deletion_date"));
                        String name = current_path.getName();
                        pathFavorites.add(CartConstructor.initialized(id, name, current_path, initial_path, deletion_date));
                    }
                    cursor.close();
                }
            }
        } finally {
            close();
        }
        return pathFavorites;
    }

    // Добавление файла в корзину
    public void addToFCart(File initial_path) {
        try {
            if (openOrInitializeDatabase()) {
                ContentValues values = new ContentValues();
                values.put("current_path", CURRENT_PATH);
                values.put("initial_path", initial_path.getAbsolutePath());
                values.put("deletion_date", System.currentTimeMillis());
                mDataBase.insert("cart", null, values);
            }
        } finally {
            close();
        }
    }
}
