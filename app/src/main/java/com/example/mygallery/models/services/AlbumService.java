package com.example.mygallery.models.services;

import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.scaning.ScanMemoryRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlbumService extends BaseService<Model> {

    @Override
    public void getData() {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(runGetData());
        executor.shutdown();
    }

    public void findAlbums() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new ScanMemoryRunnable(context));
        executor.submit(runGetData());
        executor.shutdown();
    }

    private Runnable runGetData() {
        return () -> setData(databaseManager.getDataAlbum());
    }
}
