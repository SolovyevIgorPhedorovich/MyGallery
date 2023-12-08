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

    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    private final DatabaseCart databaseCart;
    protected final DatabaseFavorites databaseFavorites;

    public ImageService() {
        super();
        this.databaseCart = new DatabaseCart(context);
        this.databaseFavorites = new DatabaseFavorites(context);
    }

    @Override
    public void getData() {
        // Implementation for getData if needed
    }

    public void moveToCart(int position) {
        File path = new File(list.get(position).getPath().getPath());
        executeInSingleThread(() -> {
            databaseCart.addToCart(path);
            databaseFavorites.removeFromFavorites(path);
        });
    }

    public void moveToCart(List<Model> pathList) {
        executeInSingleThread(() -> {
            databaseCart.addToCart(pathList);
            databaseFavorites.removeFromFavorites(pathList);
        });
    }

    @Override
    public void clear() {
        shutdownNow();
        super.clear();
    }

    public void updateFavorites(int position, File newPath) {
        File path = new File(list.get(position).getPath().getPath());
        executeInSingleThread(() -> databaseFavorites.updatePath(path, newPath));
    }

    public void updateFavorites(List<Model> pathList, List<File> newPathList) {
        executeInSingleThread(() -> databaseFavorites.updatePath(pathList, newPathList));
    }

    public void setFavorites(Model image) {
        ((Image) image).isFavorite = !((Image) image).isFavorite;
        executeInSingleThread(() -> databaseFavorites.addToFavorites(image));
    }

    public void scanMediaAlbum(File path) {
        shutdownAndExecuteNew(new ScanMediaFile(context, this, path));
    }

    public void setArtwork(File path) {
        databaseManager.setArtwork(path);
    }

    // Завершаем текущую задачу, если она активна
    private void shutdownNow() {
        executor.shutdownNow();
    }

    public void shutdownAndExecuteNew(Runnable newTask) {
        shutdownNow();
        // Создаем новый ExecutorService, чтобы можно было запустить новую задачу
        executor = executeInSingleThread(newTask);
    }
}