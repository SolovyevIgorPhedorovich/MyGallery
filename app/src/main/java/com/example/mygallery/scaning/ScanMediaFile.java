package com.example.mygallery.scaning;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
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
        List<Model> imageList = queryMediaStoreForImages();
        setService(imageList);
    }

    // Получаем список изображений из хранилища медиафайлов
    private List<Model> queryMediaStoreForImages() {
        List<Model> imageList = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};
        String selection = path.equals(Environment.getExternalStorageDirectory()) ? MediaStore.Images.Media.DATA + " LIKE ?" : MediaStore.Images.Media.DATA + " LIKE ? AND " + MediaStore.Images.Media.DATA + " NOT LIKE ?";
        String[] selectionArgs = path.equals(Environment.getExternalStorageDirectory()) ? new String[]{path.getAbsolutePath() + "/%"} : new String[]{path.getAbsolutePath() + "/%", path.getAbsolutePath() + "/%/%"};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder)) {
            if (cursor != null) {
                int dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int sizeIndex = cursor.getColumnIndex(MediaStore.Images.Media.SIZE);
                while (cursor.moveToNext()) {
                    interrupt();
                    File filePath = new File(cursor.getString(dataIndex));
                    int size = cursor.getInt(sizeIndex);
                    String name = filePath.getName();
                    Image image = createImage(imageList.size(), name, filePath, size);
                    imageList.add(image);
                    if (imageList.size() % 50 == 0) {
                        setService(imageList);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return imageList;
    }

    private void interrupt() throws Exception {
        if (Thread.interrupted()) {
            // Обработка прерывания
            throw new InterruptedException("Thread was interrupted");
        }
    }

    // Создаем объект Image с указанными параметрами
    private Image createImage(int id, String name, File path, int size) throws FileNotFoundException {
        return ImageFileConstructor.create(id + 1, name, path, size, databaseManager.checkFileFavorites(path));
    }

    // Устанавливаем список изображений в сервис
    private void setService(List<Model> imageList) {
        service.setData(imageList);
    }
}