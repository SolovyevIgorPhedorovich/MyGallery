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
        CURRENT_PATH = context.getFilesDir() + "/Корзина/";
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

    public void removeFile(List<Model> initialPathList) {
        try {
            if (openOrInitializeDatabase()) {
                mDataBase.beginTransaction();
                for (Model initialPath : initialPathList) {
                    remove(initialPath.getPath());
                }
                mDataBase.setTransactionSuccessful();
            }
        } finally {
            mDataBase.endTransaction();
            close();
        }
    }

    public void removeFile(File initialPath) {
        try {
            if (openOrInitializeDatabase()) {
                remove(initialPath);
            }
        } finally {
            close();
        }
    }

    private void remove(File initialPath) {
        mDataBase.delete("cart", "path=?", new String[]{String.valueOf(initialPath)});
    }

    // Добавление файла в корзину
    public void addToCart(File initialPath) {
        try {
            if (openOrInitializeDatabase()) {
                add(initialPath);
            }
        } finally {
            close();
        }
    }

    public void addToCart(List<Model> initialPathList) {
        try {
            if (openOrInitializeDatabase()) {
                mDataBase.beginTransaction();
                for (Model initialPath : initialPathList) {
                    add(initialPath.getPath());
                }
                mDataBase.setTransactionSuccessful();
            }
        } finally {
            mDataBase.endTransaction();
            close();
        }
    }

    private void add(File initialPath) {
        ContentValues values = new ContentValues();
        values.put("current_path", CURRENT_PATH + initialPath.getName());
        values.put("initial_path", initialPath.getAbsolutePath());
        values.put("deletion_date", System.currentTimeMillis());
        mDataBase.insert("cart", null, values);
    }
}
