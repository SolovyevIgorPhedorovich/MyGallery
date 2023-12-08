package com.example.mygallery.models;

import com.example.mygallery.interfaces.model.Model;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public class Cart implements Model {
    public int id;

    public String name;

    public File currentPath;

    public File initialPath;

    public long deletionDate;


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
        return currentPath;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setPath(File path) {
        this.currentPath = path;
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

        Cart other = (Cart) obj;

        return Objects.equals(currentPath, other.currentPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPath);
    }

    @NotNull
    @Override
    public Cart clone() {
        Cart cart = new Cart();
        cart.id = this.id;
        cart.name = this.name;
        cart.currentPath = this.currentPath;
        cart.initialPath = this.initialPath;
        cart.deletionDate = this.deletionDate;
        return cart;
    }
}