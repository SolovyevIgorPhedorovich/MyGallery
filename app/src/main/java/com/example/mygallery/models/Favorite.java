package com.example.mygallery.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.mygallery.interfaces.model.Model;

import java.io.File;
import java.util.Objects;


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
        return null;
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Favorite other = (Favorite) obj;

        return Objects.equals(this.path, other.path);
    }

    @NonNull
    public Favorite clone() {
        Favorite favorite = new Favorite();
        favorite.id = this.id;
        favorite.name = this.name;
        favorite.path = this.path;
        favorite.size = this.size;
        return favorite;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path);
    }
}
