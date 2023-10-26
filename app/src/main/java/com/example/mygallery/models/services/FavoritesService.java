package com.example.mygallery.models.services;

import com.example.mygallery.database.DatabaseFavorites;
import com.example.mygallery.interfaces.model.Model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesService extends BaseService<Model> {

    DatabaseFavorites databaseFavorites;

    @Override
    public void getData() {
        this.databaseFavorites = new DatabaseFavorites(context);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new LoadFavoriteData());
        executor.shutdown();
    }

    class LoadFavoriteData implements Runnable {

        @Override
        public void run() {
            setData(databaseFavorites.getFavorites());
        }
    }
}
