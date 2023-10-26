package com.example.mygallery.models;

import com.example.mygallery.interfaces.model.Model;

import java.io.File;

public class Album implements Model {
    public int id;

    public String name;

    public File path;
    public File artwork;
    public int count;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public File getPath() {
        return path;
    }
}
