package com.example.mygallery.models;

import com.example.mygallery.interfaces.model.Model;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;


public class Video implements Model {
    public int id;

    public String name;

    public File path;

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
        // You can implement date retrieval logic here if needed
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Video other = (Video) obj;

        return Objects.equals(path, other.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @NotNull
    @Override
    public Video clone() {
        Video video = new Video();
        video.id = this.id;
        video.name = this.name;
        video.path = this.path;
        video.size = this.size;
        video.isFavorite = this.isFavorite;
        video.time = this.time;
        return video;
    }
}
