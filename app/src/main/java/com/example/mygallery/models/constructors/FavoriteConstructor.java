package com.example.mygallery.models.constructors;

import com.example.mygallery.models.Favorite;

import java.io.File;

public class FavoriteConstructor {
    private final Favorite favorite;

    private FavoriteConstructor(int id, String name, File path, int size) {
        favorite = new Favorite();

        favorite.id = id;

        favorite.name = name;

        favorite.path = path;

        favorite.size = size;
    }

    public static Favorite initialized(int id, String name, File path, int size) {
        FavoriteConstructor favoriteConstructor = new FavoriteConstructor(id, name, path, size);
        return favoriteConstructor.returnFavorite();
    }

    private Favorite returnFavorite() {
        return favorite;
    }
}
