package com.example.mygallery.models.constructors;

import com.example.mygallery.models.Image;

import java.io.File;
import java.io.FileNotFoundException;

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

    public static Image initialized(int id, String name, File path, int size, boolean isFavorite) throws FileNotFoundException {
        if (path.exists()) {
            ImageFileConstructor imageConstructor = new ImageFileConstructor(id, name, path, size, isFavorite);
            return imageConstructor.returnImage();
        } else {
            throw new FileNotFoundException("Файл не найден: " + path);
        }
    }

    private Image returnImage() {
        return image;
    }
}
