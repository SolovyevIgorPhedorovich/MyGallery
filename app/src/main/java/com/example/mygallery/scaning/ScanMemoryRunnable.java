package com.example.mygallery.scaning;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.example.mygallery.database.DatabaseAlbum;
import com.example.mygallery.models.Album;
import com.example.mygallery.models.constructors.AlbumConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScanMemoryRunnable implements Runnable {
    private final List<Album> data;
    private final Context context;
    private final DatabaseAlbum databaseManager;

    public ScanMemoryRunnable(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        this.databaseManager = new DatabaseAlbum(context);
    }

    @Override
    public void run() {
        scanDirectoriesForFoldersWithImages();
        addFilesToDatabase();
    }

    // Метод для сканирования имен и путей папок с изображениями в хранилище
    private void scanDirectoriesForFoldersWithImages() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder)) {
            if (cursor != null) {

                int dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int folderIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                if (dataIndex == -1 || folderIndex == -1) {
                    // Обработка случая, когда нужные столбцы не найдены
                    return;
                }

                Map<String, Integer> folderFileCountMap = new LinkedHashMap<>();

                while (cursor.moveToNext()) {
                    String nameFolder = cursor.getString(folderIndex);
                    String imagePath = cursor.getString(dataIndex);

                    if (!isImageInUnwantedFolder(imagePath)) {
                        String folderPath = imagePath.substring(0, imagePath.lastIndexOf("/"));
                        int count = folderFileCountMap.merge(folderPath, 1, Integer::sum);

                        if (count == 1) {
                            data.add(AlbumConstructor.create(data.size() + 1, nameFolder, new File(folderPath), 0, new File(imagePath)));
                        }
                    }
                }

                for (Album album : data) {
                    String key = album.path.getAbsolutePath();
                    album.count = folderFileCountMap.getOrDefault(key, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для проверки, находится ли изображение в нежелательной папке
    private boolean isImageInUnwantedFolder(String imagePath) {
        return imagePath.contains("/Android/") || imagePath.contains("/.");
    }

    // Метод для добавления данных в базу данных
    private void addFilesToDatabase() {
        databaseManager.insertOrUpdateData(data);
    }
}