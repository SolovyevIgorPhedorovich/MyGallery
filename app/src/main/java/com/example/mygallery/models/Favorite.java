package com.example.mygallery.models;

import com.example.mygallery.interfaces.model.Model;


public class Favorite implements Model {
    public int id;

    public String name;

    public java.io.File path;

    public int size;

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
