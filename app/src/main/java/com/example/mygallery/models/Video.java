package com.example.mygallery.models;

import androidx.annotation.Nullable;
import com.example.mygallery.interfaces.model.Model;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;


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

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setPath(File path) {
        this.path = path;
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Video other = (Video) obj;

        return Objects.equals(this.path, other.path);
    }

    @NotNull
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

    @Override
    public int hashCode() {
        return Objects.hashCode(path);
    }
}
