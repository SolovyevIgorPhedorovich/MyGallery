package com.example.mygallery.models.constructors;

import com.example.mygallery.models.Image;

import java.io.File;

public class ImageFileConstructor {

    private final Image image;

    private ImageFileConstructor(int id, String name, File path, int size, boolean isFavorite) {
        image = new Image();

        image.id = id;

        image.name = name;

        image.path = path;

        image.size = size;

        image.isFavorite = isFavorite;
    }

    public static Image initialized(int id, String name, File path, int size, boolean isFavorite) {
        ImageFileConstructor imageConstructor = new ImageFileConstructor(id, name, path, size, isFavorite);
        return imageConstructor.returnImage();
    }

    private Image returnImage() {
        return image;
    }
}
