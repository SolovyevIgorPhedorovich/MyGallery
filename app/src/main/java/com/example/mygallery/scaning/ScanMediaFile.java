package com.example.mygallery.scaning;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.mygallery.database.DatabaseFavorites;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Image;
import com.example.mygallery.models.constructors.ImageFileConstructor;
import com.example.mygallery.models.services.BaseService;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ScanMediaFile implements Runnable {
    private final Context context;
    private final File path;
    private final BaseService<Model> service;
    private final DatabaseFavorites databaseManager;

    public ScanMediaFile(Context context, BaseService<Model> service, File path) {
        this.context = context;
        this.path = path;
        this.service = service;
        this.databaseManager = new DatabaseFavorites(context);
    }

    @Override
    public void run() {
        scanOneDirectory();
    }

    private void scanOneDirectory() {
        queryMediaStoreForImages();
    }

    private void queryMediaStoreForImages() {
        List<Model> imageList = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};
        String selection = MediaStore.Images.Media.DATA + " LIKE ? AND " + MediaStore.Images.Media.DATA + " NOT LIKE ?";
        String[] selectionArgs = new String[]{path.getAbsolutePath() + "/%", path.getAbsolutePath() + "/%/%"};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder)) {
            if (cursor != null) {
                int dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int sizeIndex = cursor.getColumnIndex(MediaStore.Images.Media.SIZE);
                while (cursor.moveToNext()) {
                    File path = new File(cursor.getString(dataIndex));
                    int size = cursor.getInt(sizeIndex);
                    String name = path.getName();
                    Image image = setItemList(imageList.size() + 1, name, path, size);
                    imageList.add(image);
                    if (imageList.size() % 50 == 0) {
                        setService(imageList);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        setService(imageList);
    }

    private Image setItemList(int id, String name, File path, int size) throws FileNotFoundException {
        return ImageFileConstructor.initialized(id, name, path, size, databaseManager.checkFileFavorites(path));
    }

    private void setService(List<Model> imageList) {
        service.setData(imageList);
    }
}

