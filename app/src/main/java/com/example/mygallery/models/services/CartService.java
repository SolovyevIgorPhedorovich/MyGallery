package com.example.mygallery.models.services;

import com.example.mygallery.database.DatabaseCart;
import com.example.mygallery.interfaces.model.Model;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartService extends BaseService<Model> {
    private final DatabaseCart databaseCart;

    public CartService() {
        super();
        databaseCart = new DatabaseCart(context);
    }

    @Override
    public void getData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> setData(databaseCart.getCartFile()));
        executor.shutdown();
    }

    public void updateDatabase(int position) {
        File path = new File(list.get(position).getPath().getPath());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> databaseCart.removeFile(path));
        executor.shutdown();
    }

    public void updateDatabase(List<Model> modelList) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> databaseCart.removeFile(modelList));
        executor.shutdown();
    }
}
