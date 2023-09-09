package com.example.mygallery.managers;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private  List<String> filePaths;
    private Context context;
    private DatabaseManager databaseManager;
    private DataManager dataManager;

    public FileManager(Context context){
        this.context = context;
        databaseManager = DatabaseManager.getInstance(context);
        dataManager = DataManager.getInstance(context);
    }

    public FileManager(Context context, List<String> selectedFilePaths){
        databaseManager = DatabaseManager.getInstance(context);
        dataManager = DataManager.getInstance(context);
        filePaths = new ArrayList<>(selectedFilePaths);
    }

    //Перемещение файла
    public void moveFile(File sourcePath, File destPath){
        destPath = new File(destPath.getAbsolutePath()+"/"+sourcePath.getName());
        renameFile(sourcePath, destPath);
    }

    //Удаление файла
    public void deleteFile(File filePath){
        File trashPath = new File (databaseManager.getFolderPath("Корзина") + "/" + filePath.getName());
        if (filePath != trashPath){
            renameFile(filePath, trashPath);
        }
        else{
            try {
                FileUtils.delete(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //Копирование файла
    public void copyFile(File sourcePath, File destPath){
        destPath = new File (destPath.getAbsolutePath() + "/"+ sourcePath.getName());
        try{
            FileUtils.copyFile(sourcePath, destPath);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    //Переименование файла
    public void renameFile(File filePath, File newFile){
        try {
            FileUtils.copyFile(filePath, newFile);
            dataManager.getPathsFiles().remove(filePath);
            FileUtils.delete(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getFilePaths(){
        return filePaths;
    }
}
