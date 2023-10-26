package com.example.mygallery;

import android.app.Application;
import android.content.Context;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Album;
import com.example.mygallery.models.services.*;
import com.example.mygallery.sharedpreferences.SharedPreferencesHelper;

import java.io.File;

public class App extends Application {
    private static App instance;
    public BaseService<Album> albums;
    public BaseService<Model> images;
    public BaseService<Model> cart;
    public BaseService<Model> favorites;
    private Context appContext;

    public static App getInstance() {
        return instance;
    }

    public Context getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appContext = getApplicationContext();

        if (!SharedPreferencesHelper.areSharedPreferencesInitialized(appContext)) {
            SharedPreferencesHelper.initializeSharedPreferences(appContext);
        }

        initializedMode();
        createTrashDir();
    }

    private void initializedMode() {
        albums = new AlbumService();
        images = new ImageService();
        cart = new CartService();
        favorites = new FavoritesService();
    }


    // Создание корзины
    private void createTrashDir() {
        File binFolder = new File(getFilesDir(), "Корзина");
        try {
            if (!binFolder.exists()) {
                binFolder.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
