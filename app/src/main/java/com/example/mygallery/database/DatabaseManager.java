package com.example.mygallery.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class DatabaseManager extends SQLiteOpenHelper {
    // Константы для базы данных
    private static final String DB_NAME = "paths_files_db";
    private static String DB_PATH;
    private static final int DATABASE_VERSION = 1;
    protected SQLiteDatabase mDataBase;
    private final Context context;

    // Конструктор класса
    public DatabaseManager(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.context = context;
        copyDataBase();
        this.getReadableDatabase();
    }

    // Создание базы данных (не используется)
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    // Обновление базы данных (не используется)
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // Открытие базы данных
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

    // Проверка наличия базы данных
    private boolean checkDataBase() {
        File dbFile = new File(getDataBasePath());
        return dbFile.exists();
    }

    // Копирование файла базы данных из ресурсов
    private void copyDataBase() {
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
        try (InputStream input = context.getAssets().open(DB_NAME);
             OutputStream output = Files.newOutputStream(Paths.get(getDataBasePath()))) {

            byte[] buffer = new byte[1024];
            int length;

            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
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

