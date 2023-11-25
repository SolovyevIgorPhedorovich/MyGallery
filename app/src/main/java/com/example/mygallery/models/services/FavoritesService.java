package com.example.mygallery.models.services;

import com.example.mygallery.database.DatabaseFavorites;
import com.example.mygallery.interfaces.model.Model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesService extends BaseService<Model> {

    private final DatabaseFavorites databaseFavorites;

    public FavoritesService() {
        databaseFavorites = new DatabaseFavorites(context);
    }

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
}
