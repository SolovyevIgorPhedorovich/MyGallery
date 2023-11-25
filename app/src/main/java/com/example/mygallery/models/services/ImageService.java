package com.example.mygallery.models.services;

import com.example.mygallery.database.DatabaseCart;
import com.example.mygallery.database.DatabaseFavorites;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.scaning.ScanMediaFile;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageService extends BaseService<Model> {

    private final DatabaseCart databaseCart;
    private final DatabaseFavorites databaseFavorites;

    public ImageService() {
        super();
        databaseCart = new DatabaseCart(context);
        databaseFavorites = new DatabaseFavorites(context);
    }

    @Override
    public void getData() {
    }

    public void moveToCart(int position) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            File path = list.get(position).getPath();
            databaseCart.addToCart(path);
            databaseFavorites.removedFromFavorites(path);
        });
        executor.shutdown();
    }

    public void moveToCart(List<Model> pathList) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            databaseCart.addToCart(pathList);
            databaseFavorites.removedFromFavorites(pathList);
        });
        executor.shutdown();
    }

    public void setFavorites(Model image) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> databaseFavorites.addToFavorites(image));
        executor.shutdown();
    }

    public void scanMediaAlbum(File path) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new ScanMediaFile(context, this, path));
        executor.shutdown();
    }
}
