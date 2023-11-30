package com.example.mygallery.models.services;

import com.example.mygallery.database.DatabaseCart;
import com.example.mygallery.database.DatabaseFavorites;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Image;
import com.example.mygallery.scaning.ScanMediaFile;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageService extends BaseService<Model> {

    protected final DatabaseCart databaseCart;
    protected final DatabaseFavorites databaseFavorites;

    public ImageService() {
        super();
        databaseCart = new DatabaseCart(context);
        databaseFavorites = new DatabaseFavorites(context);
    }

    @Override
    public void getData() {
    }

    public void moveToCart(int position) {
        File path = new File(list.get(position).getPath().getPath());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
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

    public void updateFavorites(int position, File newPath) {
        File path = new File(list.get(position).getPath().getPath());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            databaseFavorites.updatePath(path, newPath);
        });
        executor.shutdown();
    }

    public void updateFavorites(List<Model> pathList, List<File> newPathList) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            databaseFavorites.updatePath(pathList, newPathList);
        });
        executor.shutdown();
    }

    public void setFavorites(Model image) {
        ((Image) image).isFavorite = !((Image) image).isFavorite;
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
