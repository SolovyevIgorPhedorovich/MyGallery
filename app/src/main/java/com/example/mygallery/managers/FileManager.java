package com.example.mygallery.managers;

import android.content.Context;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private  List<String> filePaths;
    private DatabaseManager databaseManager;
    private DataManager dataManager;

    public FileManager(Context context){
        databaseManager = DatabaseManager.getInstance(context);
    }

    public FileManager(Context context, List<String> selectedFilePaths){
        databaseManager = DatabaseManager.getInstance(context);
        dataManager = DataManager.getInstance();
        filePaths = new ArrayList<>(selectedFilePaths);
    }

    //Перемещение файла
    public void moveFile(String sourcePath, String destPath){
        File sourceFile = new File(sourcePath);
        File destFile = new File(destPath);
        if (sourceFile.renameTo(destFile)) {

        }
        else{

        }
    }

    //Удаление файла
    public void deleteFile(File filePath){
        File fileTrashPath = new File (databaseManager.getFolderPath("Корзина") + "/" + filePath.getName());
        try {
            FileUtils.copyFile(filePath, fileTrashPath);
            dataManager.getPathsFiles().remove(filePath);
            FileUtils.delete(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Копирование файла
    public void copyFile(String sourcePath, String destPath){
        File sourceFile = new File(sourcePath);
        File destFile = new File(destPath);
        try{
            Files.copy(sourceFile.toPath(), destFile.toPath());
        }
        catch (IOException e){

        }
    }

    //Переименование файла
    public void renameFile(String filePath, String newFileName){
        File file = new File(filePath);
        File newFile = new File(newFileName);
        if (file.renameTo(newFile)){

        }
        else{

        }
    }

    public List<String> getFilePaths(){
        return filePaths;
    }
}
