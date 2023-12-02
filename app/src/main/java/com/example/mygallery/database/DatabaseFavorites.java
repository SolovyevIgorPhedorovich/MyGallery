package com.example.mygallery.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.constructors.ImageFileConstructor;

import java.io.File;
import java.io.FileNotFoundException;
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
                        try {
                            pathFavorites.add(ImageFileConstructor.initialized(id, name, path, 0, true));
                        } catch (FileNotFoundException e) {
                            mDataBase.delete("favorites", "path=?", new String[]{String.valueOf(path)});
                        }
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

                if (cursor.getCount() != 0) {
                    cursor.close();
                    return true;
                }
            }
        } finally {
            close();
        }
        return false;
    }

    public void removedFromFavorites(File path) {
        try {
            if (openOrInitializeDatabase()) {
                mDataBase.delete("favorites", "path=?", new String[]{String.valueOf(path)});
            }
        } finally {
            close();
        }
    }

    public void removedFromFavorites(List<Model> imageList) {
        try {
            if (openOrInitializeDatabase()) {
                mDataBase.beginTransaction();
                for (Model image : imageList) {
                    mDataBase.delete("favorites", "path=?", new String[]{String.valueOf(image.getPath())});
                }
                mDataBase.setTransactionSuccessful();
            }
        } finally {
            mDataBase.endTransaction();
            close();
        }
    }

    // Добавление файла в избранное
    public void addToFavorites(Model image) {
        try {
            if (openOrInitializeDatabase()) {
                ContentValues values = new ContentValues();
                values.put("path", image.getPath().getAbsolutePath());
                mDataBase.insertOrThrow("favorites", null, values);
            }
        } catch (SQLiteConstraintException e) {
            removedFromFavorites(image.getPath());
        } finally {
            close();
        }
    }

    public void updatePath(File oldPath, File newPath) {
        try {
            if (openOrInitializeDatabase()) {
                ContentValues values = new ContentValues();
                values.put("path", newPath.getAbsolutePath());
                mDataBase.update("favorites", values, "path=?", new String[]{oldPath.getAbsolutePath()});
            }
        } finally {
            close();
        }
    }

    public void updatePath(List<Model> oldImageList, List<File> newImageList) {
        try {
            if (openOrInitializeDatabase()) {
                ContentValues values = new ContentValues();
                mDataBase.beginTransaction();
                for (int i = 0, a = 1; i < oldImageList.size(); i++, a += 2) {
                    values.put("path", newImageList.get(a).getAbsolutePath());
                    mDataBase.update("favorites", values, "path=?", new String[]{oldImageList.get(i).getPath().getAbsolutePath()});
                }
                mDataBase.setTransactionSuccessful();
            }
        } finally {
            mDataBase.endTransaction();
            close();
        }
    }
}
