package com.example.mygallery.models;

import com.example.mygallery.interfaces.model.Model;


public class Image implements Model {
    public int id;

    public String name;

    public java.io.File path;

    public int size;
    public boolean isFavorite;


    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public java.io.File getPath() {
        return path;
    }

}
