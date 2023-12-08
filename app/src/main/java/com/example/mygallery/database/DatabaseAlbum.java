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
    // Define table names
    private static final String TABLE_FOLDERS = "folders";
    private static final String TABLE_ARTWORK = "artwork";

    // Define column names for 'folders' table
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_COUNT_FILES = "count_files";
    private static final String COLUMN_PATH = "path";

    // Define column names for 'artwork' table
    private static final String COLUMN_ID_FOLDER = "id_folder";
    private static final String COLUMN_ARTWORK_PATH = "path";

    public DatabaseAlbum(Context context) {
        super(context);
    }

    // Получение ID папки по её пути
    private long getAlbumId(String path) {
        long folderId = -1;
        if (openOrInitializeDatabase()) {
            try (Cursor cursor = mDataBase.rawQuery("SELECT id FROM folders WHERE path = ?", new String[]{path})) {
                if (cursor.moveToFirst()) {
                    folderId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                }
            }
        }
        return folderId;
    }

    private int getCount(String path) {
        int count = 0;
        if (openOrInitializeDatabase()) {
            try (Cursor cursor = mDataBase.rawQuery("SELECT count_files FROM folders WHERE path = ?", new String[]{path})) {
                if (cursor.moveToFirst()) {
                    count = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT_FILES));
                }
            }
        }
        return count;
    }

    // Вставка данных о папке в базу данных
    private void startInsert(Album data) {
        ContentValues valuesFolders = new ContentValues();
        ContentValues valuesArtwork = new ContentValues();

        valuesFolders.put(COLUMN_NAME, data.name);
        valuesFolders.put(COLUMN_COUNT_FILES, data.count);
        valuesFolders.put(COLUMN_PATH, String.valueOf(data.path));
        valuesArtwork.put(COLUMN_ARTWORK_PATH, String.valueOf(data.artwork));

        insertOrUpdateDataFolders(valuesFolders, valuesArtwork);
    }

    public void updateData(String curPath, String destPath, int count) {
        if (openOrInitializeDatabase()) {
            mDataBase.beginTransaction();
            try {
                updateCount(curPath, getCount(curPath) - count);
                updateCount(destPath, getCount(destPath) + count);

                mDataBase.setTransactionSuccessful();
            } finally {
                mDataBase.endTransaction();
                close();
            }
        }
    }

    private void updateCount(String path, int count) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_COUNT_FILES, count);
        mDataBase.update(TABLE_FOLDERS, values, COLUMN_PATH + "=?", new String[]{path});
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
                close();
            }
        }
    }

    private void insertOrUpdateDataFolders(ContentValues valuesFolders, ContentValues valuesArtwork) {
        String path = valuesFolders.getAsString(COLUMN_PATH);
        long id = mDataBase.update(TABLE_FOLDERS, valuesFolders, COLUMN_PATH + "=?", new String[]{path});

        if (id == 0) {
            // Записи с таким "path" не существует, поэтому создадим новую запись.
            insertDataFolders(valuesFolders, valuesArtwork);
        } else {
            if (!getArtworkAlbumPaths(id).exists()) {
                setArtwork(getAlbumId(valuesFolders.getAsString(COLUMN_PATH)), valuesArtwork);
            }
        }
    }

    private void insertDataFolders(ContentValues valuesFolders, ContentValues valuesArtwork) {
        try {
            long id = mDataBase.insert(TABLE_FOLDERS, null, valuesFolders);
            valuesArtwork.put(COLUMN_ID_FOLDER, id);
            // Вставка информации об обложках (artworks)
            mDataBase.insert(TABLE_ARTWORK, null, valuesArtwork);
        } catch (SQLiteConstraintException e) {
            String errorMessage = e.getMessage();

            // Обработка ошибки, например, изменение имени папки и повторная вставка
            if (errorMessage != null && errorMessage.contains("UNIQUE constraint")) {
                handleConstraintException(valuesFolders, valuesArtwork);
            }
        }
    }

    private void handleConstraintException(ContentValues valuesFolders, ContentValues valuesArtwork) {
        String name = valuesFolders.getAsString(COLUMN_NAME);
        try (Cursor cursor = mDataBase.query(TABLE_FOLDERS, null, COLUMN_NAME + " LIKE ?", new String[]{name + "%"}, null, null, null)) {
            if (cursor != null) {
                String newName = name + " " + cursor.getCount();
                valuesFolders.put(COLUMN_NAME, newName);
                insertDataFolders(valuesFolders, valuesArtwork);
            }
        }
    }

    private List<String> getExistingFolders() {
        List<String> existingFolders = new ArrayList<>();
        try (Cursor cursor = mDataBase.rawQuery("SELECT path FROM folders", null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String oldPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATH));
                    existingFolders.add(oldPath);
                }
            }
        }
        return existingFolders;
    }

    private void deleteMissingFolders(List<String> foldersToDelete) {
        if (!foldersToDelete.isEmpty()) {
            String whereClause = COLUMN_PATH + " IN (" + TextUtils.join(",", Collections.nCopies(foldersToDelete.size(), "?")) + ")";
            mDataBase.delete(TABLE_FOLDERS, whereClause, foldersToDelete.toArray(new String[0]));
        }
    }

    // Получение путей обложек альбомов
    private File getArtworkAlbumPaths(long id) {
        try (Cursor cursor = mDataBase.rawQuery("SELECT path FROM artwork WHERE id_folder=?", new String[]{String.valueOf(id)})) {
            if (cursor.moveToFirst()) {
                return new File(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTWORK_PATH)));
            }
        }
        return null;
    }

    // Получение информации о папках
    public List<Model> getDataAlbum() {
        List<Model> list = new ArrayList<>();
        if (openOrInitializeDatabase()) {
            try (Cursor cursor = mDataBase.rawQuery("SELECT id, name, count_files, path FROM folders GROUP BY id", null)) {
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                        int count = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT_FILES));
                        File path = new File(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATH)));
                        File artwork = getArtworkAlbumPaths(id);
                        list.add(AlbumConstructor.create(id, name, path, count, artwork));
                    }
                }
            } finally {
                close();
            }
        }
        return list;
    }

    private void setArtwork(long id, ContentValues values) {
        if (values.getAsString(COLUMN_ARTWORK_PATH) != null) {
            mDataBase.update(TABLE_ARTWORK, values, COLUMN_ID_FOLDER + "=?", new String[]{String.valueOf(id)});
        }
    }

    // Обновление обложки
    public void setArtwork(File path) {
        try {
            if (openOrInitializeDatabase()) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_ARTWORK_PATH, path.getAbsolutePath());
                setArtwork(getAlbumId(path.getParent()), values);
            }
        } finally {
            close();
        }
    }
}