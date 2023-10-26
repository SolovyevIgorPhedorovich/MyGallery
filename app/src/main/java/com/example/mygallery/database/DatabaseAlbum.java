package com.example.mygallery.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.text.TextUtils;
import android.util.Log;
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
    public long getAlbumId(String findData) {
        long folderId = -1;
        if (openOrInitializeDatabase()) {
            Cursor cursor = mDataBase.rawQuery("SELECT id FROM folders WHERE name = ?", new String[]{findData});
            if (cursor.moveToFirst()) {
                folderId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            }
            cursor.close();
        }
        return folderId;
    }

    // Вставка данных о папке в базу данных
    public long insertData(String nameFolder, int countFiles, String path) {
        long id = -1;
        if (openOrInitializeDatabase()) {
            ContentValues values = new ContentValues();
            values.put("name", nameFolder);
            values.put("count_files", countFiles);
            values.put("path", path);
            id = mDataBase.insert("folders", null, values);
        }
        return id;
    }

    // Вставка данных о нескольких папках в базу данных
    public void insertOrUpdateData(List<String> names, List<Integer> counts, List<String> paths, List<String> artworks) {
        if (openOrInitializeDatabase()) {
            mDataBase.beginTransaction();
            try {
                // Получите список всех папок из базы данных
                List<String> existingFolders = getExistingFolders();

                for (int i = 0; i < names.size(); i++) {
                    String name = names.get(i);
                    int count = counts.get(i);
                    String pathFolder = paths.get(i);
                    String pathCovers = artworks.get(i);

                    ContentValues valuesFolders = new ContentValues();
                    ContentValues valuesArtwork = new ContentValues();

                    valuesFolders.put("name", name);
                    valuesFolders.put("count_files", count);
                    valuesFolders.put("path", pathFolder);
                    valuesArtwork.put("path", pathCovers);

                    insertOrUpdateDataFolders(valuesFolders, valuesArtwork, pathFolder);

                    // Удалите информацию о папке из списка существующих
                    existingFolders.remove(pathFolder);
                }

                // Удалите информацию о папках, которые есть в базе данных, но отсутствуют в списке сканирования
                deleteMissingFolders(existingFolders);

                mDataBase.setTransactionSuccessful();
            } finally {
                mDataBase.endTransaction();
                Log.d("Updata DB", "База данных обновлена");
            }
        }
    }

    private void insertOrUpdateDataFolders(ContentValues valuesFolders, ContentValues valuesArtwork, String pathFolder) {
        if (openOrInitializeDatabase()) {
            mDataBase.beginTransaction();
            try {
                long id = 0;
                // Попытка обновления записи с заданным "path". Если записи с таким "path" нет, она будет вставлена.
                id = mDataBase.update("folders", valuesFolders, "path = ?", new String[]{pathFolder});

                if (id == 0) {
                    // Записи с таким "path" не существует, поэтому создадим новую запись.
                    id = mDataBase.insert("folders", null, valuesFolders);
                    valuesArtwork.put("id_folder", id);
                    // Вставка информации об обложках (artworks)
                    mDataBase.insert("artwork", null, valuesArtwork);
                }


                mDataBase.setTransactionSuccessful();
            } catch (SQLiteConstraintException e) {
                String errorMessage = e.getMessage();

                // Обработка ошибки, например, изменение имени папки и повторная вставка
                if (errorMessage != null && errorMessage.contains("UNIQUE constraint")) {
                    renameNameDataFolder(valuesFolders, valuesArtwork, pathFolder);
                }
            } finally {
                mDataBase.endTransaction();
            }
        }
    }

    // Переименование папки в случае конфликта имени
    private void renameNameDataFolder(ContentValues valuesFolders, ContentValues valuesArtwork, String pathFolder) {
        String name = valuesFolders.getAsString("name");
        Cursor cursor = mDataBase.query("folders", null, "name LIKE ?", new String[]{name + "%"}, null, null, null);

        if (cursor != null) {
            // Изменяем имя папки, добавляем нумерацию
            String newName = name + " " + cursor.getCount();
            valuesFolders.put("name", newName);

            // Повторно пытаемся вставить данные
            insertOrUpdateDataFolders(valuesFolders, valuesArtwork, pathFolder);
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
    public List<Album> getDataAlbum() {
        List<Album> list = new ArrayList<>();

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
            close();
        }
        return list;
    }

    // Обновление обложки
    public int updateArtwork(String name, String newPath) {
        long id = -1;
        if (openOrInitializeDatabase()) {
            ContentValues values = new ContentValues();
            values.put("path", newPath);
            id = mDataBase.update("artwork", values, "folder_id = ?", new String[]{String.valueOf(getAlbumId(name))});
            close();
        }
        return (int) id;
    }
}
