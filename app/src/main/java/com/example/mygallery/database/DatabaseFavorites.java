package com.example.mygallery.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Image;
import com.example.mygallery.models.constructors.FavoriteConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFavorites extends DatabaseManager {
    public DatabaseFavorites(Context context) {
        super(context);
    }

    // Получение списка избранных файлов
    public List<Model> getFavorites() {
        List<Model> pathFavorites = new ArrayList<>();
        try {
            if (openOrInitializeDatabase()) {
                Cursor cursor = mDataBase.rawQuery("SELECT id, path FROM favorites", null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                        File path = new File(cursor.getString(cursor.getColumnIndexOrThrow("path")));
                        String name = path.getName();
                        pathFavorites.add(FavoriteConstructor.initialized(id, name, path, 0));
                    }
                    cursor.close();
                }
            }
        } finally {
            close();
        }
        return pathFavorites;
    }

    public boolean checkFileFavorites(File path) {
        try {
            if (openOrInitializeDatabase()) {
                Cursor cursor = mDataBase.rawQuery("SELECT path FROM favorites WHERE path=?", new String[]{path.getAbsolutePath()});

                if (cursor != null) {
                    cursor.close();
                    return true;
                }
            }
        } finally {
            close();
        }
        return false;
    }

    // Добавление файла в избранное
    public void addToFavorites(Image image) {
        try {
            if (openOrInitializeDatabase()) {
                ContentValues values = new ContentValues();
                values.put("path", image.path.getAbsolutePath());
                mDataBase.insert("favorites", null, values);
            }
        } finally {
            close();
        }
    }
}
