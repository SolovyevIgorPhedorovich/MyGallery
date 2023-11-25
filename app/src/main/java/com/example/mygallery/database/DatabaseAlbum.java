package com.example.mygallery.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.text.TextUtils;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Album;
import com.example.mygallery.models.constructors.AlbumConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseAlbum extends DatabaseManager {
    public DatabaseAlbum(Context context) {
        super(context);
    }

    // Получение ID папки по её имени
    private long getAlbumId(String name) {
        long folderId = -1;
        if (openOrInitializeDatabase()) {
            Cursor cursor = mDataBase.rawQuery("SELECT id FROM folders WHERE name = ?", new String[]{name});
            if (cursor.moveToFirst()) {
                folderId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            }
            cursor.close();
        }
        return folderId;
    }

    private int getCount(String path) {
        int count = 0;
        if (openOrInitializeDatabase()) {
            Cursor cursor = mDataBase.rawQuery("SELECT count_files FROM folders WHERE path = ?", new String[]{path});
            if (cursor.moveToFirst()) {
                count = cursor.getInt(cursor.getColumnIndexOrThrow("count_files"));
            }
            cursor.close();
        }
        return count;
    }

    // Вставка данных о папке в базу данных
    private void startInsert(Album data) {
        ContentValues valuesFolders = new ContentValues();
        ContentValues valuesArtwork = new ContentValues();

        valuesFolders.put("name", data.name);
        valuesFolders.put("count_files", data.count);
        valuesFolders.put("path", String.valueOf(data.path));
        valuesArtwork.put("path", String.valueOf(data.artwork));

        insertOrUpdateDataFolders(valuesFolders, valuesArtwork);
    }

    public void updateData(String curPath, String destPath, int count) {
        if (openOrInitializeDatabase()) {
            mDataBase.beginTransaction();
            try {

                ContentValues dest = new ContentValues();

                if (curPath != null) {
                    ContentValues cur = new ContentValues();
                    cur.put("count_files", getCount(curPath) - count);
                    mDataBase.update("folders", cur, "path=?", new String[]{curPath});
                }

                dest.put("count_files", getCount(destPath) + count);
                mDataBase.update("folders", dest, "path=?", new String[]{destPath});

                mDataBase.setTransactionSuccessful();
            } finally {
                mDataBase.endTransaction();
            }
            close();
        }
    }

    // Вставка данных о нескольких папках в базу данных
    public void insertOrUpdateData(List<Album> dataList) {
        if (openOrInitializeDatabase()) {
            mDataBase.beginTransaction();
            try {
                // Получите список всех папок из базы данных
                List<String> existingFolders = getExistingFolders();

                for (Album data : dataList) {
                    startInsert(data);

                    // Удалите информацию о папке из списка существующих
                    existingFolders.remove(String.valueOf(data.path));
                }

                // Удалите информацию о папках, которые есть в базе данных, но отсутствуют в списке сканирования
                deleteMissingFolders(existingFolders);

                mDataBase.setTransactionSuccessful();
            } finally {
                mDataBase.endTransaction();
            }
            close();
        }
    }

    private void insertOrUpdateDataFolders(ContentValues valuesFolders, ContentValues valuesArtwork) {
        // Попытка обновления записи с заданным "path". Если записи с таким "path" нет, она будет вставлена.
        String path = valuesFolders.getAsString("path");
        long id = mDataBase.update("folders", valuesFolders, "path = ?", new String[]{path});

        if (id == 0) {
            // Записи с таким "path" не существует, поэтому создадим новую запись.
            insertDataFolders(valuesFolders, valuesArtwork);
        } else {
            updateArtwork(getAlbumId(valuesFolders.getAsString("name")), valuesArtwork);
        }
    }

    private void insertDataFolders(ContentValues valuesFolders, ContentValues valuesArtwork) {
        try {
            long id = mDataBase.insert("folders", null, valuesFolders);
            valuesArtwork.put("id_folder", id);
            // Вставка информации об обложках (artworks)
            mDataBase.insert("artwork", null, valuesArtwork);
        } catch (SQLiteConstraintException e) {
            String errorMessage = e.getMessage();

            // Обработка ошибки, например, изменение имени папки и повторная вставка
            if (errorMessage != null && errorMessage.contains("UNIQUE constraint")) {
                renameNameDataFolder(valuesFolders, valuesArtwork);
            }
        }

    }

    // Переименование папки в случае конфликта имени
    private void renameNameDataFolder(ContentValues valuesFolders, ContentValues valuesArtwork) {
        String name = valuesFolders.getAsString("name");
        Cursor cursor = mDataBase.query("folders", null, "name LIKE ?", new String[]{name + "%"}, null, null, null);

        if (cursor != null) {
            // Изменяем имя папки, добавляем нумерацию
            String newName = name + " " + cursor.getCount();
            valuesFolders.put("name", newName);

            // Повторно пытаемся вставить данные
            insertDataFolders(valuesFolders, valuesArtwork);
            cursor.close();
        }
    }

    private List<String> getExistingFolders() {
        List<String> existingFolders = new ArrayList<>();
        Cursor cursor = mDataBase.rawQuery("SELECT path FROM folders", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String oldPath = cursor.getString(cursor.getColumnIndexOrThrow("path"));
                existingFolders.add(oldPath);
            }
            cursor.close();
        }

        return existingFolders;
    }

    private void deleteMissingFolders(List<String> foldersToDelete) {
        if (!foldersToDelete.isEmpty()) {
            String whereClause = "path IN (" + TextUtils.join(",", Collections.nCopies(foldersToDelete.size(), "?")) + ")";
            mDataBase.delete("folders", whereClause, foldersToDelete.toArray(new String[0]));
        }
    }

    // Получение путей обложек альбомов
    private File getArtworkAlbumPaths(int id) {
        Cursor cursor = mDataBase.rawQuery("SELECT path FROM artwork WHERE id_folder=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            File artwork = new File(cursor.getString(cursor.getColumnIndexOrThrow("path")));
            cursor.close();
            return artwork;
        }
        return null;
    }

    // Получение информации о папках
    public List<Model> getDataAlbum() {
        List<Model> list = new ArrayList<>();
        try {
            if (openOrInitializeDatabase()) {
                Cursor cursor = mDataBase.rawQuery("SELECT id, name, count_files, path FROM folders GROUP BY id", null);

                if (cursor != null) {

                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                        int count = cursor.getInt(cursor.getColumnIndexOrThrow("count_files"));
                        File path = new File(cursor.getString(cursor.getColumnIndexOrThrow("path")));
                        File artwork = getArtworkAlbumPaths(id);

                        list.add(AlbumConstructor.initialized(id, name, path, count, artwork));
                    }
                    cursor.close();
                }
            }
        } finally {
            close();
        }
        return list;
    }

    private void updateArtwork(long id, ContentValues values) {
        if (values.getAsString("path") != null) {
            mDataBase.update("artwork", values, "id_folder=?", new String[]{String.valueOf(id)});
        }
    }

    // Обновление обложки
    public void updateArtwork(String name, String newPath) {
        try {
            if (openOrInitializeDatabase()) {
                ContentValues values = new ContentValues();
                values.put("path", newPath);
                updateArtwork(getAlbumId(name), values);
            }
        } finally {
            close();
        }
    }
}
