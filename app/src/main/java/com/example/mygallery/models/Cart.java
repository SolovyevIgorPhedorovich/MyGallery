package com.example.mygallery.models;

import com.example.mygallery.interfaces.model.Model;

import java.io.File;

public class Cart implements Model {
    public int id;

    public String name;

    public File current_path;

    public File initial_path;

    public long deletion_date;


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
        return current_path;
    }
}
