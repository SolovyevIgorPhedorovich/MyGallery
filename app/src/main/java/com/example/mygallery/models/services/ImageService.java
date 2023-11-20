package com.example.mygallery.models.services;

import com.example.mygallery.database.DatabaseCart;
import com.example.mygallery.database.DatabaseFavorites;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Image;
import com.example.mygallery.scaning.ScanMediaFile;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageService extends BaseService<Model> {

    @Override
    public void getData() {
    }

    @Override
    public void removeItem(int position) {
        super.removeItem(position);
        DatabaseCart databaseCart = new DatabaseCart(context);
        databaseCart.addToFCart((list.get(position)).getPath());
    }

    public void setFavorites(Image image) {
        DatabaseFavorites databaseFavorites = new DatabaseFavorites(context);
        databaseFavorites.addToFavorites(image);
    }

    public void scanMediaAlbum(File path) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new ScanMediaFile(context, this, path));
        executor.shutdown();
    }
}
