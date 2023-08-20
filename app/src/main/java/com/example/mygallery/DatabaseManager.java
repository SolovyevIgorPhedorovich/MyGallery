package com.example.mygallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import com.bumptech.glide.disklrucache.DiskLruCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DB_NAME = "paths_files_db";
    private static String DB_PATH = "";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase mDataBase;
    private final Context context;
    private DataManager dataManager;

    public DatabaseManager(Context context){
        super(context, DB_NAME, null, DATABASE_VERSION);
        DB_PATH = context.getApplicationInfo().dataDir+"/databases/";
        this.context = context;
        copyDataBase();
        this.getReadableDatabase();
        dataManager = DataManager.getInstance();
    }

    private boolean checkDataBase(){
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    public void copyDataBase(){
        if(!checkDataBase()){
            this.getReadableDatabase();
            this.close();
            try{
                copyDataBaseFile();
            }
            catch (IOException e){
                throw new Error("ErrorCopingDataBase");
            }
        }
    }

    public void getFileInFolder(int position){
        if (openDataBase()){
            Cursor cursor = mDataBase.rawQuery("SELECT path FROM paths WHERE folder_id=?", new String[]{String.valueOf(getIdFolder(dataManager.getNamesFolders().get(position), dataManager.getCountFiles().get(position)))});
            dataManager.clearData(DataManager.PATH);
            if (cursor != null){
                while (cursor.moveToNext()){
                    dataManager.getPathsFiles().add(cursor.getString(cursor.getColumnIndexOrThrow("path")));
                }
            }
            cursor.close();
        }
        close();
    }

    private void copyDataBaseFile() throws IOException{
        InputStream input = context.getAssets().open(DB_NAME);
        OutputStream output = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] buffer = new byte[1024];
        int length;

        while ((length = input.read(buffer)) > 0){
            output.write(buffer, 0, length);
        }

        output.flush();
        output.close();
        input.close();
    }

    public boolean openDataBase() throws SQLException{
        if(mDataBase == null || !mDataBase.isOpen())
            mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    public long getIdFolder(String findData, int countFiles){
        long folderId = -1;
        if (openDataBase()) {
            Cursor cursor = mDataBase.rawQuery("SELECT id FROM folders WHERE name = ?", new String[]{findData});
            if (cursor.moveToFirst()) {
                folderId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            }
            cursor.close();
        }
        return folderId;
    }

    public long insertFolders(String nameFolder, int countFiles, String path){
        long id = -1;
        if (openDataBase()){
            ContentValues values = new ContentValues();
            values.put("name", nameFolder);
            values.put("count_files", countFiles);
            values.put("path_folder", path);
            id = mDataBase.insert("folders", null, values);
        }
        return id;
    }

    public void insertFile(String path, String nameFolder, int countFiles){
        if (openDataBase()){
            ContentValues values = new ContentValues();
            values.put("path", path);
            values.put("folder_id",getIdFolder(nameFolder, countFiles));
            mDataBase.insert("paths", null, values);
        }
    }

    public void insertMultipleDataFolders(){
        String name, path;
        int count;
        mDataBase = this.getWritableDatabase();
        mDataBase.beginTransaction();
        try{
            for (int i = 0; i < dataManager.getNamesFolders().size() - 1; i++){
                name = dataManager.getNamesFolders().get(i);
                count = dataManager.getCountFiles().get(i);
                path = dataManager.pathsFolders.get(i);
                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("count_files", count);
                values.put("path_folder", path);
                mDataBase.insert("folders", null, values);
            }
            mDataBase.setTransactionSuccessful();
        }
        finally {
            mDataBase.endTransaction();
            dataManager.pathsFolders = null;
            close();
        }
    }

    public void insertMultipleDataPath(){
        mDataBase = this.getWritableDatabase();
        mDataBase.beginTransaction();
        try{
            for (String path: dataManager.getPathsFiles()) {
                ContentValues values = new ContentValues();;
                values.put("path", path);
                values.put("folder_id", getIdFolder(getNameFolder(path), 0));
                mDataBase.insert("paths", null,  values);
            }
            mDataBase.setTransactionSuccessful();
        } finally {
            mDataBase.endTransaction();
            close();
        }
    }

    private String getNameFolder(String path){
        String[] pathParts = path.split("/");
        return pathParts[pathParts.length - 2];
    }

    private void getCoversAlbumPaths() {
        Cursor cursor = mDataBase.rawQuery("SELECT cover_path FROM folder_covers", new String[]{});
        if (cursor != null){
            while (cursor.moveToNext()){
                dataManager.addDataInCoversFolders(cursor.getString(cursor.getColumnIndexOrThrow("cover_path")));
            }
        }
        cursor.close();
    }
    public void getFolder(){
        if (openDataBase()){
            Cursor cursor = mDataBase.rawQuery("SELECT name, count_files FROM folders WHERE count_files != 0 GROUP BY id", new String[]{});
            if (cursor != null){
                while (cursor.moveToNext()){
                    dataManager.addDataInNamesFolders(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    dataManager.addDataInCountFiles(cursor.getInt(cursor.getColumnIndexOrThrow("count_files")));
                }
            }
            cursor.close();
            getCoversAlbumPaths();
        }
    }

    @Override
    public synchronized void close(){
        if (mDataBase != null){
            mDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
