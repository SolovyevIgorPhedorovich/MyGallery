package com.example.mygallery;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import com.example.mygallery.activities.AlbumActivity;
import com.example.mygallery.managers.DataManager;
import com.example.mygallery.managers.DatabaseManager;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScanMemoryRunnable implements Runnable{
    private DataManager dataManager;
    private DatabaseManager databaseManager;
    private Context context;

    public ScanMemoryRunnable(Context context){
        dataManager = DataManager.getInstance(context);
        databaseManager = DatabaseManager.getInstance(context);
        this.context = context;
    }

    //Cканирование имен и пути папок
    private void scanDirectoriesForFoldersWithImages(File directory) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor != null && cursor.moveToFirst()){
            Map<String, Integer> folderFileCountMap = new LinkedHashMap<>();
            while (cursor.moveToNext()) {
                int dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int folderIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                String nameFolder = cursor.getString(folderIndex);
                String imagePath = cursor.getString(dataIndex);
                String pathFolder = new File(imagePath).getParent();
                if (!isImageInUnwantedFolder(imagePath)) {
                    if (folderFileCountMap.containsKey(pathFolder)){
                        int currentCount = folderFileCountMap.get(pathFolder);
                        folderFileCountMap.put(pathFolder, currentCount + 1);
                    }
                    else {
                        dataManager.getPathsFolders().add(pathFolder);
                        dataManager.getNamesFolders().add(nameFolder);
                        dataManager.getCoversFolders().add(imagePath);
                        folderFileCountMap.put(pathFolder, 1);
                    }
                }
            }
            for (int count: folderFileCountMap.values()){
                dataManager.getCountFiles().add(count);
            }
        }
        cursor.close();
    }

    private boolean isImageInUnwantedFolder(String imagePath){
        return imagePath.contains("/Android/") || imagePath.contains("/.");
    }

    private void addFilesDataBase() {
        Log.d("addFileDataBase", "Start");
        databaseManager.checkedData();
        databaseManager.insertMultipleDataFolders();
    }

    @Override
    public void run() {
        File directory = Environment.getExternalStorageDirectory();
        dataManager.clearData();
        if (directory != null && directory.exists()) {
            scanDirectoriesForFoldersWithImages(directory);
            addFilesDataBase();
        }
        databaseManager.close();
        ((AlbumActivity)context).updateAdapter();
    }


}
