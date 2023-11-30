package com.example.mygallery.models;

import androidx.annotation.Nullable;
import com.example.mygallery.interfaces.model.Model;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Date;
import java.util.Objects;

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

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setPath(File path) {
        this.current_path = path;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public String getDate() {
        return null;
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Cart other = (Cart) obj;

        return Objects.equals(this.current_path, other.current_path);
    }

    @NotNull
    public Cart clone() {
        Cart cart = new Cart();
        cart.id = this.id;
        cart.name = this.name;
        cart.current_path = this.current_path;
        cart.initial_path = this.initial_path;
        cart.deletion_date = this.deletion_date;
        return cart;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(current_path);
    }
}
