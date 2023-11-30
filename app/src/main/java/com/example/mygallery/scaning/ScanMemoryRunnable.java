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
        this.databaseManager = new DatabaseAlbum((context));
    }

    @Override
    public void run() {
        scanDirectoriesForFoldersWithImages();
        addFilesToDatabase();
    }

    // Метод для сканирования имен и путей папок с изображениями в хранилище
    private void scanDirectoriesForFoldersWithImages() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; // URI для доступа к изображениям во внешнем хранилище
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME}; // Запрашиваемые столбцы
        String selection = null; // Условие выборки (пустое, чтобы выбрать все изображения)
        String[] selectionArgs = null; // Аргументы условия выборки
        String sortOrder = null; // Сортировка результатов (по умолчанию)

        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder)) {
            if (cursor != null) {
                Map<String, Integer> folderFileCountMap = new LinkedHashMap<>(); // Карта для отслеживания количества файлов в каждой папке
                while (cursor.moveToNext()) {
                    int dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA); // Индекс столбца с путем к изображению
                    int folderIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME); // Индекс столбца с именем папки

                    String nameFolder = cursor.getString(folderIndex); // Имя текущей папки
                    String imagePath = cursor.getString(dataIndex); // Путь к текущему изображению
                    String folderPath = new File(imagePath).getParent(); // Путь к текущей папке

                    if (!isImageInUnwantedFolder(imagePath)) { // Проверка, что изображение не находится в нежелательной папке
                        int count = getCountFile(folderFileCountMap.getOrDefault(folderPath, 0));

                        folderFileCountMap.put(folderPath, count); // Обновляем счетчика файлов в папке
                        if (count == 1) { // Если это первое изображение в папке
                            data.add(AlbumConstructor.initialized(data.size() + 1, nameFolder, new File(folderPath), 0, new File(imagePath)));
                        }
                    }
                }

                int i = 0;
                for (int count : folderFileCountMap.values()) {
                    data.get(i).count = count;
                    i++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Обработка возможных ошибок и вывод информации о них
        }
    }

    private int getCountFile(Integer count) {
        return (count != null) ? count + 1 : 1;
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