package com.example.mygallery.managers;

import android.content.Context;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private final DatabaseManager databaseManager;
    private final DataManager dataManager;
    private List<String> fileToDeleteList;

    public FileManager(Context context){
        databaseManager = DatabaseManager.getInstance(context);
        dataManager = DataManager.getInstance(context);
    }

    public FileManager(Context context, List<String> selectedFileToDeletes) {
        this(context);
        fileToDeleteList = new ArrayList<>(selectedFileToDeletes);
    }

    // Перемещение файла
    public void moveFile(File sourceFile, File destPath) {
        destPath = new File(destPath, sourceFile.getName());
        renameFile(sourceFile, destPath);
    }

    // Удаление файла
    public void deleteFile(File fileToDelete) {
        File trashPath = new File(databaseManager.getFolderPath("Корзина"), fileToDelete.getName());
        if (!fileToDelete.equals(trashPath)) {
            renameFile(fileToDelete, trashPath);
        } else {
            try {
                FileUtils.delete(fileToDelete);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Копирование файла
    public void copyFile(File sourceFile, File destPath) {
        destPath = new File(destPath, sourceFile.getName());
        try {
            FileUtils.copyFile(sourceFile, destPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Переименование файла
    public void renameFile(File fileToDelete, File newFileName) {
        try {
            FileUtils.copyFile(fileToDelete, newFileName);
            dataManager.getImageFilesList().remove(fileToDelete);
            FileUtils.delete(fileToDelete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getFileToDeletes() {
        return fileToDeleteList;
    }
}