package com.example.mygallery.models.services;

import com.example.mygallery.database.DatabaseCart;
import com.example.mygallery.interfaces.model.Model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartService extends BaseService<Model> {
    private DatabaseCart databaseCart;


    @Override
    public void getData() {
        databaseCart = new DatabaseCart(context);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new LoadCartData());
        executor.shutdown();
    }

    class LoadCartData implements Runnable {
        @Override
        public void run() {
            setData(databaseCart.getCartFile());
        }

    }
}
