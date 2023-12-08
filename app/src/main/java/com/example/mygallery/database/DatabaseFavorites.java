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
    private static final String TABLE_NAME_FAVORITES = "favorites";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PATH = "path";

    public DatabaseFavorites(Context context) {
        super(context);
    }

    // Метод для выполнения запроса к базе данных
    private Cursor queryFavorites(String[] columns, String selection, String[] selectionArgs) {
        if (openOrInitializeDatabase()) {
            return mDataBase.query(TABLE_NAME_FAVORITES, columns, selection, selectionArgs, null, null, null);
        }
        return null;
    }

    // Метод для закрытия базы данных и курсора
    private void closeDatabaseAndCursor(Cursor cursor) {
        close();
        if (cursor != null) {
            cursor.close();
        }
    }

    // Получение списка избранных файлов из базы данных
    public List<Model> getFavorites() {
        List<Model> pathFavorites = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = queryFavorites(new String[]{COLUMN_ID, COLUMN_PATH}, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    File path = new File(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATH)));
                    String name = path.getName();
                    try {
                        // Создание объекта Model и добавление его в список избранных файлов
                        pathFavorites.add(ImageFileConstructor.create(id, name, path, 0, true));
                    } catch (FileNotFoundException e) {
                        // Если файл не найден, удаляем его из базы данных
                        mDataBase.delete(TABLE_NAME_FAVORITES, COLUMN_PATH + "=?", new String[]{String.valueOf(path)});
                    }
                }
            }
        } finally {
            closeDatabaseAndCursor(cursor);
        }

        return pathFavorites;
    }

    // Внутренний метод для проверки наличия файла в избранных
    private boolean checkFileFavoritesInternal(File path) {
        Cursor cursor = null;

        try {
            cursor = queryFavorites(new String[]{COLUMN_PATH}, COLUMN_PATH + "=?", new String[]{path.getAbsolutePath()});
            return cursor != null && cursor.getCount() != 0;
        } finally {
            closeDatabaseAndCursor(cursor);
        }
    }

    // Проверка наличия файла в избранных
    public boolean checkFileFavorites(File path) {
        return openOrInitializeDatabase() && checkFileFavoritesInternal(path);
    }

    // Удаление файла из избранных
    public void removeFromFavorites(File path) {
        try {
            if (openOrInitializeDatabase()) {
                mDataBase.delete(TABLE_NAME_FAVORITES, COLUMN_PATH + "=?", new String[]{String.valueOf(path)});
            }
        } finally {
            close();
        }
    }

    // Удаление файлов из избранных
    public void removeFromFavorites(List<Model> imageList) {
        try {
            if (openOrInitializeDatabase()) {
                mDataBase.beginTransaction();
                for (Model image : imageList) {
                    mDataBase.delete(TABLE_NAME_FAVORITES, COLUMN_PATH + "=?", new String[]{String.valueOf(image.getPath())});
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
                // Попытка добавления файла в базу данных избранных
                ContentValues values = values(image.getPath());
                mDataBase.insertOrThrow(TABLE_NAME_FAVORITES, null, values);
            }
        } catch (SQLiteConstraintException e) {
            // Если файл уже существует в избранных, удаляем его
            removeFromFavorites(image.getPath());
        } finally {
            close();
        }
    }

    // Обновление пути файла в избранных
    public void updatePath(File oldPath, File newPath) {
        try {
            if (openOrInitializeDatabase()) {
                update(values(newPath), oldPath);
            }
        } finally {
            close();
        }
    }

    // Обновление путей файлов в избранных
    public void updatePath(List<Model> oldImageList, List<File> newImageList) {
        try {
            if (openOrInitializeDatabase()) {
                mDataBase.beginTransaction();
                for (int i = 0, a = 1; i < oldImageList.size(); i++, a += 2) {
                    // Обновление пути файла в базе данных избранных
                    update(values(newImageList.get(a)), oldImageList.get(i).getPath());
                }
                mDataBase.setTransactionSuccessful();
            }
        } finally {
            mDataBase.endTransaction();
            close();
        }
    }

    private void update(ContentValues values, File oldPath) {
        mDataBase.update(TABLE_NAME_FAVORITES, values, COLUMN_PATH + "=?", new String[]{oldPath.getAbsolutePath()});
    }

    private ContentValues values(File path) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PATH, path.getAbsolutePath());
        return values;
    }
}