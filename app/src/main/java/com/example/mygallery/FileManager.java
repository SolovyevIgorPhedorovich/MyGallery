package com.example.mygallery;

import android.content.SharedPreferences;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private  List<String> filePaths;
    private String trashPath;

    public FileManager(String path){
        trashPath = path;
    }

    public FileManager(List<String> selectedFilePaths){
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
    public void deleteFile(String filePath){
        File file = new File(filePath);
        File fileTrash = new File("");
        if(file.renameTo(fileTrash)){

        }
        else{

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
