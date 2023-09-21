package com.example.mygallery.managers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class DatabaseManager extends SQLiteOpenHelper {
    // Константы для базы данных
    private static final String DB_NAME = "paths_files_db";
    private static String DB_PATH;
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase mDataBase;
    private final Context context;
    @SuppressLint("StaticFieldLeak")
    private static DatabaseManager instance;
    private final DataManager dataManager;

    // Конструктор класса
    public DatabaseManager(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.context = context;
        copyDataBase();
        this.getReadableDatabase();
        dataManager = DataManager.getInstance(context);
        instance = this;
    }

    // Получение единственного экземпляра DatabaseManager
    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    // Создание базы данных (не используется)
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    // Обновление базы данных (не используется)
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // Метод для открытия базы данных
    public boolean openOrInitializeDatabase() throws SQLException {
        if (mDataBase == null || !mDataBase.isOpen())
            mDataBase = SQLiteDatabase.openDatabase(getDataBasePath(), null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    // Получение пути к базе данных
    private String getDataBasePath() {
        // Проверка и получение пути к базе данных
        if (TextUtils.isEmpty(DB_PATH)) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        }
        return DB_PATH + DB_NAME;
    }

    // Получение ID папки по её имени
    public long getIdFolder(String findData) {
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
    public long insertFolders(String nameFolder, int countFiles, String path) {
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
    public void insertMultipleDataFolders() {
        if (openOrInitializeDatabase()) {
            mDataBase.beginTransaction();
            try {
                List<String> namesFolderNamesList = dataManager.getFolderNamesList();
                List<Integer> fileCountList = dataManager.getFileCountList();
                List<String> folderPathsList = dataManager.getFolderPathsList();
                List<String> folderCoversList = dataManager.getFolderCoversList();

                for (int i = 0; i < namesFolderNamesList.size(); i++) {
                    String name = namesFolderNamesList.get(i);
                    int count = fileCountList.get(i);
                    String pathFolder = folderPathsList.get(i);
                    String pathCovers = folderCoversList.get(i);

                    ContentValues valuesFolders = new ContentValues();
                    ContentValues valuesArtwork = new ContentValues();

                    valuesFolders.put("name", name);
                    valuesFolders.put("count_files", count);
                    valuesFolders.put("path", pathFolder);
                    valuesArtwork.put("path", pathCovers);

                    insertDataFolders(valuesFolders, valuesArtwork, name);
                }
                mDataBase.setTransactionSuccessful();
            } finally {
                mDataBase.endTransaction();
                dataManager.clearData(DataManager.PATH_FOLDERS);
            }
        }
    }

    // Проверка наличия базы данных
    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    // Копирование файла базы данных из ресурсов
    public void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDataBaseFile();
            } catch (IOException e) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    // Копирование файла базы данных из ресурсов
    private void copyDataBaseFile() throws IOException {
        InputStream input = context.getAssets().open(DB_NAME);
        OutputStream output = new FileOutputStream(getDataBasePath());
        byte[] buffer = new byte[1024];
        int length;

        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }

        output.flush();
        output.close();
        input.close();
    }

    // Получение списка избранных файлов
    public List<File> getFavorites(){
        List<File> pathFavorites = new ArrayList<>();
        if (openOrInitializeDatabase()) {
            Cursor cursor = mDataBase.rawQuery("SELECT path FROM favorites", null);
            if (cursor != null){
                while (cursor.moveToNext()){
                    pathFavorites.add(new File(cursor.getString(cursor.getColumnIndexOrThrow("path"))));
                }
                cursor.close();
            }
        }
        close();
        return pathFavorites;
    }

    // Вставка данных о папке и её обложке в базу данных
    private void insertDataFolders(ContentValues valuesFolders, ContentValues valuesArtwork, String name){
        long newId;
        try {
            newId = mDataBase.insert("folders", null, valuesFolders);
            valuesArtwork.put("id_folder", newId);
            mDataBase.insert("artwork", null, valuesArtwork);
        } catch (SQLiteConstraintException e) {
            String errorMessage = e.getMessage();

            // Обработка ошибки, например, изменение имени папки и повторная вставка
            if (errorMessage != null && errorMessage.contains("UNIQUE constraint")) {
                renameNameDataFolder(valuesFolders, valuesArtwork, name);
            }
        }
    }

    // Переименование папки в случае конфликта имени
    private void renameNameDataFolder(ContentValues valuesFolders, ContentValues valuesArtwork, String name){
        Cursor cursor = mDataBase.query("folders", null, "name LIKE ?", new String[]{name + "%"}, null, null, null);

        if (cursor != null){
            // Изменяем имя папки, например, добавляем нумерацию
            String newName = name + " " + cursor.getCount();
            valuesFolders.put("name", newName);

            // Повторно пытаемся вставить данные
            insertDataFolders(valuesFolders, valuesArtwork, name);
            cursor.close();
        }
    }

    // Получение путей обложек альбомов
    private void getCoversAlbumPaths() {
        Cursor cursor = mDataBase.rawQuery("SELECT path FROM artwork GROUP BY id_folder", null);
        if (cursor != null){
            while (cursor.moveToNext()){
                dataManager.addPathToFolderCoversList(cursor.getString(cursor.getColumnIndexOrThrow("path")));
            }
            cursor.close();
        }
    }

    // Получение информации о папках
    public void getFolder(){
        if (openOrInitializeDatabase()) {
            Cursor cursor = mDataBase.rawQuery("SELECT name, count_files FROM folders GROUP BY id", null);
            if (cursor != null){
                while (cursor.moveToNext()){
                    dataManager.addNameToFolderNamesList(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    dataManager.addCountToFileCountList(cursor.getInt(cursor.getColumnIndexOrThrow("count_files")));
                }
                cursor.close();
            }
            getCoversAlbumPaths();
        }
    }

    // Получение пути к папке по имени
    public String getFolderPath(String name){
        String path = null;
        if (openOrInitializeDatabase()) {
            Cursor cursor = mDataBase.rawQuery("SELECT path FROM folders WHERE name=?", new String[]{name});
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndexOrThrow("path"));
            }
            cursor.close();
            close();
        }
        return path;
    }

    // Обновление пути к папке
    public int updatePath (String name, String newPath){
        long id = -1;
        if (openOrInitializeDatabase()) {
            ContentValues values = new ContentValues();
            values.put("path", newPath);
            id = mDataBase.update("artwork", values, "folder_id = ?", new String[]{String.valueOf(getIdFolder(name))});
            close();
        }
        return (int)id;
    }

    // Получение всех путей
    private List<String> getAllPath(){
        List<String> name = null;
        if (openOrInitializeDatabase()) {
            Cursor cursor = mDataBase.rawQuery("SELECT name FROM folders WHERE NOT name ==?", new String[]{"Корзина"});
            if (cursor != null) {
                name = new ArrayList<>();
                while (cursor.moveToNext()) {
                    name.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                }
                cursor.close();
            }
        }
        return name;
    }

    // Проверка данных и удаление устаревших папок
    public void checkedData (){
        comparedFolder();
    }

    // Сравнение папок и удаление устаревших
    private void comparedFolder(){
        List<String> oldFolderNames = getAllPath();
        List<String> obsoleteFolderIds = new ArrayList<>();
        boolean isFound = false;

        for (String oldName : oldFolderNames) {
            for (String newName : dataManager.getFolderNamesList()) {
                if (oldName.equals(newName)){
                    isFound = true;
                    break;
                }
            }
            if (!isFound){
                obsoleteFolderIds.add(String.valueOf(getIdFolder(oldName)));
            }
            else{
                isFound = false;
            }
        }

        if (!obsoleteFolderIds.isEmpty()) {
            removedOldFolder(obsoleteFolderIds);
        }
    }

    // Удаление устаревших папок
    public void removedOldFolder(List<String> folderRemoved){
        if (openOrInitializeDatabase()) {
            mDataBase.beginTransaction();
            try{
                for (String id: folderRemoved){
                    mDataBase.delete("folders", "id=?", new String[]{id});
                }
                mDataBase.setTransactionSuccessful();
            }
            finally {
                mDataBase.endTransaction();
            }
        }
    }

    // Удаление устаревших файлов
    public void removedOldFile(List<String> fileRemoved){
        if (openOrInitializeDatabase()) {
            mDataBase.beginTransaction();
            try {
                for (String path: fileRemoved){
                    mDataBase.delete("paths", "path=?", new String[]{path});
                }
                mDataBase.setTransactionSuccessful();
            }
            finally {
                mDataBase.endTransaction();
                close();
            }
        }
    }

    // Добавление файла в избранное
    public void addToFavorites(String path) {
        if (openOrInitializeDatabase()) {
            ContentValues values = new ContentValues();
            values.put("path",path);
            mDataBase.insert("favorites", null, values);
            close();
        }
    }

    // Закрытие базы данных
    @Override
    public synchronized void close(){
        if (mDataBase != null){
            mDataBase.close();
        }
        super.close();
    }
}

