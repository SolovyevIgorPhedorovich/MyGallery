package com.example.mygallery.models;

import com.example.mygallery.interfaces.model.Model;


public class Video implements Model {
    public int id;

    public String name;

    public java.io.File path;

    public int size;
    public boolean isFavorite;
    public double time;


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
