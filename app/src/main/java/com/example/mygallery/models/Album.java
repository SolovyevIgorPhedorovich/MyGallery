package com.example.mygallery.models;

import com.example.mygallery.interfaces.model.Model;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

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
        // You can implement size calculation logic here if needed
        return 0;
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

        Album other = (Album) obj;

        return id == other.id &&
                Objects.equals(name, other.name) &&
                Objects.equals(path, other.path) &&
                Objects.equals(artwork, other.artwork) &&
                count == other.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NotNull
    @Override
    public Album clone() {
        Album album = new Album();
        album.id = this.id;
        album.name = this.name;
        album.path = this.path;
        album.count = this.count;
        album.artwork = this.artwork;
        return album;
    }
}