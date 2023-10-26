package com.example.mygallery.models.services;

import com.example.mygallery.database.DatabaseAlbum;
import com.example.mygallery.models.Album;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlbumService extends BaseService<Album> {

    private DatabaseAlbum databaseManager;

    @Override
    public void getData() {
        this.databaseManager = new DatabaseAlbum(context);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new GetDataAlbum());
        executor.shutdown();
    }

    class GetDataAlbum implements Runnable {
        @Override
        public void run() {
            setData(databaseManager.getDataAlbum());
        }
    }
}
