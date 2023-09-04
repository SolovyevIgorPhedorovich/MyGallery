package com.example.mygallery.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWindowAllocationException;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DB_NAME = "paths_files_db";
    private static String DB_PATH = "";
    private static final int DATABASE_VERSION = 1;

    private final Lock lock = new ReentrantLock();
    private SQLiteDatabase mDataBase;
    private final Context context;
    private DataManager dataManager;
    private static DatabaseManager instance;

    public DatabaseManager(Context context){
        super(context, DB_NAME, null, DATABASE_VERSION);
        DB_PATH = context.getApplicationInfo().dataDir+"/databases/";
        this.context = context;
        copyDataBase();
        this.getReadableDatabase();
        dataManager = DataManager.getInstance();
        instance = this;
    }

    public static DatabaseManager getInstance(Context context){
        if (instance == null){
            instance = new DatabaseManager(context);
        }
        return instance;
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

    public void getFileInFolder(String nameFolder){
        if (openDataBase()){
            Cursor cursor = mDataBase.rawQuery("SELECT path FROM paths WHERE folder_id=?", new String[]{String.valueOf(getIdFolder(nameFolder))});
            dataManager.clearData(DataManager.PATH);
            if (cursor != null){
                while (cursor.moveToNext()){
                    //dataManager.getPathsFiles().add(cursor.getString(cursor.getColumnIndexOrThrow("path")));
                }
            }
            cursor.close();
        }
    }

    private List<String> getAllFiles(){
        List<String> path = null;
        if (openDataBase()){
            Cursor cursor = mDataBase.rawQuery("SELECT path FROM paths WHERE folder_id !=?", new String[]{"1"});
            if (cursor != null){
                path = new ArrayList<>();
                while (cursor.moveToNext()){
                    path.add(cursor.getString(cursor.getColumnIndexOrThrow("path")));
                }
            }
            cursor.close();
        }
        close();
        return path;
    }

    private List<String> getAllFilesFolder(){
        List<String> path = null;
        if (openDataBase()){
            Cursor cursor = mDataBase.rawQuery("SELECT path FROM paths", new String[]{});
            dataManager.clearData(DataManager.PATH);
            if (cursor != null){
                path = new ArrayList<>();
                while (cursor.moveToNext()){
                    path.add(cursor.getString(cursor.getColumnIndexOrThrow("path")));
                }
            }
            cursor.close();
        }
        close();
        return path;
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

    public long getIdFolder(String findData){
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

    public void setCoverAlbum(String path, String nameFolder, int countFiles){
        if (openDataBase()){
            ContentValues values = new ContentValues();
            values.put("path", path);
            values.put("folder_id",getIdFolder(nameFolder));
            mDataBase.insert("paths", null, values);
        }
    }

    public void insertMultipleDataFolders(){
        String name, path_folder, path_covers;
        int count;
        long new_id = -1;
        if(openDataBase()){
            mDataBase.beginTransaction();
            try{
                for (int i = 0; i < dataManager.getNamesFolders().size() - 1; i++){
                    name = dataManager.getNamesFolders().get(i);
                    count = dataManager.getCountFiles().get(i);
                    path_folder = dataManager.getPathsFolders().get(i);
                    path_covers = dataManager.getCoversFolders().get(i);
                    ContentValues values_folders = new ContentValues();
                    ContentValues values_paths = new ContentValues();
                    values_folders.put("name", name);
                    values_folders.put("count_files", count);
                    values_folders.put("path_folder", path_folder);
                    values_paths.put("path", path_covers);
                    try {
                        new_id = mDataBase.insert("folders", null, values_folders);
                        values_paths.put("folder_id", new_id);
                        mDataBase.insert("paths_file", null, values_paths);
                    } catch (SQLiteConstraintException e) {
                        String errorMessage = e.getMessage();

                        // Обработка ошибки, например, изменение имени папки и повторная вставка
                        if (errorMessage.contains("UNIQUE constraint")) {
                            Cursor cursor = mDataBase.query("folders", null, "name LIKE ?", new String[]{name + "%"}, null, null, null);

                            if (cursor != null){
                                // Изменяем имя папки, например, добавляем нумерацию
                                String newName = name+" ("+cursor.getCount()+")";
                                values_folders.put("name", newName);

                                // Повторно пытаемся вставить данные
                                new_id = mDataBase.insert("folders", null, values_folders);
                                values_paths.put("folder_id", new_id);
                                mDataBase.insert("paths_file", null, values_paths);
                            }
                            cursor.close();
                        }
                    }
                }
                mDataBase.setTransactionSuccessful();
            }
            finally {
                mDataBase.endTransaction();
                dataManager.clearData(DataManager.PATH_FOLDERS);
            }
        }
    }

    private String getNameFolder(String path){
        String[] pathParts = path.split("/");
        return pathParts[pathParts.length - 2];
    }

    private void getCoversAlbumPaths() {
        Cursor cursor = mDataBase.rawQuery("SELECT path FROM paths_file GROUP BY folder_id", null);
        if (cursor != null){
            while (cursor.moveToNext()){
                dataManager.addDataInCoversFolders(cursor.getString(cursor.getColumnIndexOrThrow("path")));
            }
        }
        cursor.close();
    }

    public void getFolder(){
        if (openDataBase()){
            Cursor cursor = mDataBase.rawQuery("SELECT name, count_files FROM folders WHERE count_files != 0 GROUP BY id", null);
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

    public String getFolderPath(String name){
        String path = null;
        if (openDataBase()){
            Cursor cursor = mDataBase.rawQuery("SELECT path_folder FROM folders WHERE name=?", new String[]{name});
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndexOrThrow("path_folder"));
            }
            cursor.close();
            close();
        }
        return path;
    }

    public void updatePath (String oldPath, String newPath){
        if (openDataBase()){
            ContentValues values = new ContentValues();
            values.put("folder_id", getIdFolder(getNameFolder(newPath)));
            values.put("path", newPath);
            mDataBase.update("paths", values, "path = ?", new String[]{oldPath});
            close();
        }
    }

    private List<String> getAllPath(){
        List<String> name = null;
        if(openDataBase()) {
            Cursor cursor = mDataBase.rawQuery("SELECT name FROM folders WHERE NOT name ==?", new String[]{"Корзина"});
            if (cursor != null) {
                name = new ArrayList<>();
                while (cursor.moveToNext()) {
                    name.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                }
            }
            cursor.close();
        }
        return name;
    }

    public void checkedData (){
        comparedFolder();
    }

    private void comparedFolder(){
        List<String> nameFolder = getAllPath();
        List<String> idFolderForRemoved = new ArrayList<>();
        boolean isFound = false;

        for (String oldName: nameFolder){
            for (String newName: dataManager.getNamesFolders()){
                if (oldName.equals(newName)){
                    isFound = true;
                    break;
                }
            }
            if (!isFound){
                idFolderForRemoved.add(String.valueOf(getIdFolder(oldName)));
            }
            else{
                isFound = false;
            }
        }

        if (idFolderForRemoved.size() != 0){
            removedOldFolder(idFolderForRemoved);
        }
    }

    public void removedOldFolder(List<String> folderRemoved){
        if (openDataBase()){
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

    public void removedOldFile(List<String> fileRemoved){
        if (openDataBase()){
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
