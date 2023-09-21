package com.example.mygallery.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    // Константы для идентификации типов данных
    public static final int NAME = 0;
    public static final int PATH = 1;
    public static final int COUNT = 2;
    public static final int COVERS = 3;
    public static final int PATH_FOLDERS = 4;

    @SuppressLint("StaticFieldLeak")
    private static DataManager instance;
    private final Context context;
    private List<String> folderNamesList;
    private List<File> imageFilesList;
    private List<Integer> fileCountList;
    private List<String> folderCoversList;
    private List<String> folderPathsList;

    public DataManager(Context context) {
        this.context = context;
        folderNamesList = new ArrayList<>();
        imageFilesList = new ArrayList<>();
        folderPathsList = new ArrayList<>();
        folderCoversList = new ArrayList<>();
        fileCountList = new ArrayList<>();
        instance = this;
    }

    // Получение единственного экземпляра DataManager
    public static DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }

    // Добавление пути к списку путей к папкам
    public void addPathToFolderPathsList(String path) {
        folderPathsList.add(path);
    }

    // Установка данных о папках
    public void setFolderData(List<String> folderNamesList, List<Integer> fileCountList, List<String> folderCoversList) {
        this.folderNamesList = folderNamesList;
        this.fileCountList = fileCountList;
        this.folderCoversList = folderCoversList;
    }

    // Сканирование MediaStore для изображений
    private void scanMediaStoreForImages(File pathsFolder) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; // Задаем URI для доступа к медиа-файлам на внешнем устройстве хранения
        String[] projection = {MediaStore.Images.Media.DATA};// Задаем проекцию для получения данных о пути к файлу
        String selection = MediaStore.Images.Media.DATA + " LIKE ?";// Задаем условие выборки: файлы, пути к которым начинаются с указанной папки
        String[] selectionArgs = new String[]{pathsFolder.getAbsolutePath() + "%"};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"; // Задаем порядок сортировки по дате добавления

        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder)) {
            // Проверяем, что курсор не пустой и можно начать чтение данных
            if (cursor != null && cursor.moveToFirst()) {
                int dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA); // Индекс столбца с путем к файлу в результатах запроса

                // Перебираем результаты запроса для получения путей к изображениям
                do {
                    // Извлекаем путь к файлу из курсора и добавляем его в список изображений
                    imageFilesList.add(new File(cursor.getString(dataIndex)));
                } while (cursor.moveToNext());
            }
        }
    }

    // Сканирование папок с изображениями с использованием FileUtils
    private void scanFileUtilsForImages(File pathsFolder) {
        imageFilesList = (List<File>) FileUtils.listFiles(pathsFolder, new String[]{"png", "jpg", "jpeg"}, false);
    }

    // Установка списка изображений
    public void setImageFilesList(File pathsFolder, int position) {
        if (fileCountList.get(position) <= 100) {
            scanFileUtilsForImages(pathsFolder);
        } else {
            scanMediaStoreForImages(pathsFolder);
        }
    }

    // Получение списка имен папок
    public List<String> getFolderNamesList() {
        return folderNamesList;
    }

    // Установка списка имен папок
    public void setFolderNamesList(List<String> folderNamesList) {
        this.folderNamesList = folderNamesList;
    }

    // Получение списка изображений
    public List<File> getImageFilesList() {
        return imageFilesList;
    }

    // Установка списка изображений
    public void setImageFilesList(List<File> imageFilesList) {
        this.imageFilesList = new ArrayList<>(imageFilesList);
    }

    // Получение списка счетчиков файлов
    public List<Integer> getFileCountList() {
        return fileCountList;
    }

    // Установка списка счетчиков файлов
    public void setFileCountList(List<Integer> fileCountList) {
        this.fileCountList = fileCountList;
    }

    // Получение списка обложек папок
    public List<String> getFolderCoversList() {
        return folderCoversList;
    }

    // Установка списка обложек папок
    public void setFolderCoversList(List<String> folderCoversList) {
        this.folderCoversList = folderCoversList;
    }

    // Получение списка путей к папкам
    public List<String> getFolderPathsList() {
        return folderPathsList;
    }

    // Установка списка путей к папкам
    public void setFolderPathsList(List<String> folderPathsList) {
        this.folderPathsList = folderPathsList;
    }

    // Добавление имени в список имен папок
    public void addNameToFolderNamesList(String name) {
        folderNamesList.add(name);
    }

    // Добавление нескольких имен в список имен папок
    public void addNameToFolderNamesList(List<String> names) {
        folderNamesList.addAll(names);
    }

    // Добавление счетчика в список счетчиков файлов
    public void addCountToFileCountList(int count) {
        fileCountList.add(count);
    }

    // Добавление нескольких счетчиков в список счетчиков файлов
    public void addCountToFileCountList(List<Integer> counts) {
        fileCountList.addAll(counts);
    }

    // Добавление пути в список обложек папок
    public void addPathToFolderCoversList(String coverPath) {
        folderCoversList.add(coverPath);
    }

    // Добавление нескольких путей в список обложек папок
    public void addPathToFolderCoversList(List<String> coverPaths) {
        folderCoversList.addAll(coverPaths);
    }

    // Проверка наличия данных
    public boolean isData() {
        return isDataFolderNamesList() && isDataFileCountList() && isDataFolderCoversList() && isDataImageFilesList();
    }

    // Проверка наличия данных об именах папок
    public boolean isDataFolderNamesList() {
        return !folderNamesList.isEmpty();
    }

    // Проверка наличия данных о путях к изображениям
    public boolean isDataImageFilesList() {
        return !imageFilesList.isEmpty();
    }

    // Проверка наличия данных о счетчиках файлов
    public boolean isDataFileCountList() {
        return !fileCountList.isEmpty();
    }

    // Проверка наличия данных об обложках папок
    public boolean isDataFolderCoversList() {
        return !folderCoversList.isEmpty();
    }

    // Очистка данных
    public void clearData() {
        folderNamesList.clear();
        imageFilesList.clear();
        fileCountList.clear();
        folderCoversList.clear();
        folderPathsList.clear();
    }

    // Очистка данных по идентификатору
    public void clearData(int id) {
        switch (id) {
            case NAME:
                folderNamesList.clear();
                break;
            case PATH:
                imageFilesList.clear();
                break;
            case COUNT:
                fileCountList.clear();
                break;
            case COVERS:
                folderCoversList.clear();
                break;
            case PATH_FOLDERS:
                folderPathsList.clear();
                break;
        }
    }

    // Удаление элемента по идентификатору и позиции
    public void remove(int id, int position) {
        if (id == PATH) {
            imageFilesList.remove(position);
        }
    }
}

