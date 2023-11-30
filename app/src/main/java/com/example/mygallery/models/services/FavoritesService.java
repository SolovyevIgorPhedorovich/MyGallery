package com.example.mygallery.models.services;

import com.example.mygallery.interfaces.model.Model;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesService extends ImageService {

    @Override
    public void getData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> setData(databaseFavorites.getFavorites()));
        executor.shutdown();
    }

    public void updateDatabase(int position) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> databaseFavorites.addToFavorites(list.get(position)));
        executor.shutdown();
    }

    @Override
    public void moveToCart(int position) {
        super.moveToCart(position);
    }

    @Override
    public void moveToCart(List<Model> pathList) {
        super.moveToCart(pathList);
    }

    @Override
    public void updateFavorites(int position, File newPath) {
        super.updateFavorites(position, newPath);
    }

    @Override
    public void updateFavorites(List<Model> pathList, List<File> newPathList) {
        super.updateFavorites(pathList, newPathList);
    }
}
