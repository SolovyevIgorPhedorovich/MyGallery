package com.example.mygallery.models.services;

import com.example.mygallery.interfaces.model.Model;

import java.io.File;
import java.util.List;

public class FavoritesService extends ImageService {

    @Override
    public void getData() {
        executeInSingleThread(() -> setData(databaseFavorites.getFavorites()));
    }

    public void updateDatabase(int position) {
        super.setFavorites(list.get(position));
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
