package com.example.mygallery;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.example.mygallery.activities.AlbumActivity;
import com.example.mygallery.managers.DataManager;
import com.example.mygallery.managers.DatabaseManager;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScanMemoryRunnable implements Runnable {
    private final DataManager dataManager;
    private final DatabaseManager databaseManager;
    private final Context context;

    public ScanMemoryRunnable(Context context) {
        this.context = context;
        dataManager = DataManager.getInstance(context);
        databaseManager = DatabaseManager.getInstance(context);
    }

    @Override
    public void run() {
        dataManager.clearData();
        scanDirectoriesForFoldersWithImages();
        addFilesToDatabase();

        databaseManager.close();
        ((AlbumActivity) context).updateAdapter();
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
                    String pathFolder = new File(imagePath).getParent(); // Путь к текущей папке
                    if (!isImageInUnwantedFolder(imagePath)) { // Проверка, что изображение не находится в нежелательной папке
                        folderFileCountMap.put(pathFolder, folderFileCountMap.getOrDefault(pathFolder, 0) + 1); // Увеличение счетчика файлов в папке
                        if (folderFileCountMap.get(pathFolder) == 1) { // Если это первое изображение в папке
                            dataManager.getFolderPathsList().add(pathFolder); // Добавление пути папки в список
                            dataManager.getFolderNamesList().add(nameFolder); // Добавление имени папки в список
                            dataManager.getFolderCoversList().add(imagePath); // Добавление пути изображения в список обложек папок
                        }
                    }
                }
                dataManager.getFileCountList().addAll(folderFileCountMap.values()); // Добавление количества файлов в каждой папке в список
            }
        } catch (Exception e) {
            e.printStackTrace(); // Обработка возможных ошибок и вывод информации о них
        }
    }

    // Метод для проверки, находится ли изображение в нежелательной папке
    private boolean isImageInUnwantedFolder(String imagePath) {
        return imagePath.contains("/Android/") || imagePath.contains("/.");
    }

    // Метод для добавления данных в базу данных
    private void addFilesToDatabase() {
        Log.d("addFileDataBase", "Start");
        databaseManager.checkedData();
        databaseManager.insertMultipleDataFolders();
    }
}
