package com.example.mygallery.managers;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import com.example.mygallery.activities.CreatedAlbumActivity;
import com.example.mygallery.popupWindow.PopupWindowInputNameContextMenu;

import java.io.File;

public class CreateAlbumManager {

    public static void createAlbum(Context context, View view) {
        PopupWindowInputNameContextMenu.show(context, view, generateUniqueFolderName(), null);
    }


    public static void create(Context context, String nameAlbum) {
        File newAlbum = new File(Environment.getExternalStorageDirectory() + "/Pictures/" + nameAlbum);
        newAlbum.mkdirs();
        openDirectory(context, newAlbum);
    }

    private static void openDirectory(Context context, File path) {
        Intent intent = new Intent(context, CreatedAlbumActivity.class);
        intent.putExtra("pathAlbum", path.getAbsolutePath());
        context.startActivity(intent);
    }

    private static String generateUniqueFolderName() {
        String folderName = "Новый альбом";
        File baseDir = new File(Environment.getExternalStorageDirectory(), "/Pictures/");
        File targetDir = new File(baseDir, folderName);

        int counter = 1;
        while (targetDir.exists()) {
            folderName = "Новый альбом" + " " + counter;
            targetDir = new File(baseDir, folderName);
            counter++;
        }

        return folderName;
    }
}