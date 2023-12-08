package com.example.mygallery.models.services;

import com.example.mygallery.database.DatabaseCart;
import com.example.mygallery.interfaces.model.Model;

import java.io.File;
import java.util.List;

public class CartService extends BaseService<Model> {
    private final DatabaseCart databaseCart;

    public CartService() {
        super();
        databaseCart = new DatabaseCart(context);
    }

    @Override
    public void getData() {
        executeInSingleThread(() -> setData(databaseCart.getCartItems()));
    }

    public void updateDatabase(int position) {
        File path = new File(list.get(position).getPath().getPath());
        executeInSingleThread(() -> databaseCart.removeFile(path));
    }

    public void updateDatabase(List<Model> modelList) {
        executeInSingleThread(() -> databaseCart.removeFiles(modelList));
    }
}
