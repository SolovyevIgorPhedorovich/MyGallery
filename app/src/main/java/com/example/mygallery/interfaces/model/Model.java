package com.example.mygallery.interfaces.model;

import java.io.File;

public interface Model {
    int getId();

    File getPath();

    String getName();

    void setId(int id);

    void setPath(File path);
}
