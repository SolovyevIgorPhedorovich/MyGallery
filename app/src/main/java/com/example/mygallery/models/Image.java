package com.example.mygallery.models;

import android.icu.text.SimpleDateFormat;
import com.example.mygallery.interfaces.model.Model;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class Image implements Model {
    public int id;

    public String name;

    public File path;

    public long date;

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

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setPath(File path) {
        this.path = path;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getDate() {
        Date creationDate = new Date(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return dateFormat.format(creationDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Image other = (Image) obj;

        return Objects.equals(path, other.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @NotNull
    @Override
    public Image clone() {
        Image image = new Image();
        image.id = this.id;
        image.name = this.name;
        image.path = this.path;
        image.size = this.size;
        image.isFavorite = this.isFavorite;
        return image;
    }
}