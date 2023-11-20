package com.example.mygallery.models;

import androidx.annotation.Nullable;
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
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Album other = (Album) obj;

        return this.id == other.id
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.path, other.path)
                && Objects.equals(this.artwork, other.artwork)
                && this.count == other.count;
    }

    @NotNull
    public Album clone() {
        Album album = new Album();
        album.id = this.id;
        album.name = this.name;
        album.path = this.path;
        album.count = this.count;
        album.artwork = this.artwork;
        return album;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
