package com.example.mygallery.managers;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import com.example.mygallery.activities.CreatedAlbumActivity;
import com.example.mygallery.popupWindow.PopupWindowInputNameContextMenu;

import java.io.File;

public class CreateAlbumManager {

    // Метод для создания альбома с уникальным именем
    public void createAlbum(Context context, View view) {
        String uniqueFolderName = generateUniqueFolderName();
        PopupWindowInputNameContextMenu.show(context, view, uniqueFolderName, null);
    }

    // Метод для явного создания альбома с заданным именем
    public void create(Context context, String nameAlbum) {
        File newAlbum = createAlbumDirectory(nameAlbum);
        openDirectory(context, newAlbum);
    }

    // Вспомогательный метод для создания директории альбома
    private File createAlbumDirectory(String nameAlbum) {
        // Определяем базовую директорию для сохранения альбома
        File baseDir = new File(Environment.getExternalStorageDirectory(), "/Pictures/");
        // Создаем директорию с заданным именем
        File newAlbum = new File(baseDir, nameAlbum);
        newAlbum.mkdirs(); // Создаем директорию, включая все промежуточные
        return newAlbum;
    }

    // Вспомогательный метод для открытия директории альбома
    private void openDirectory(Context context, File path) {
        Intent intent = new Intent(context, CreatedAlbumActivity.class);
        intent.putExtra("pathAlbum", path.getAbsolutePath());
        context.startActivity(intent);
    }

    // Вспомогательный метод для генерации уникального имени альбома
    private String generateUniqueFolderName() {
        String baseFolderName = "Новый альбом";
        File baseDir = new File(Environment.getExternalStorageDirectory(), "/Pictures/");
        String uniqueFolderName = baseFolderName;

        int counter = 1;
        File targetDir = new File(baseDir, uniqueFolderName);

        while (targetDir.exists()) {
            uniqueFolderName = baseFolderName + " " + counter;
            targetDir = new File(baseDir, uniqueFolderName);
            counter++;
        }

        return uniqueFolderName;
    }
}