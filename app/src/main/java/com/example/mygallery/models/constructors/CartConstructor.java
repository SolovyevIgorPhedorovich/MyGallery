package com.example.mygallery.models.constructors;

import com.example.mygallery.models.Cart;

import java.io.File;

public class CartConstructor {

    private final Cart cart;

    private CartConstructor(int id, String name, File current_path, File initial_path, int deletion_date) {
        cart = new Cart();
        cart.id = id;
        cart.name = name;
        cart.current_path = current_path;
        cart.initial_path = initial_path;
        cart.deletion_date = deletion_date;
    }

    public static Cart initialized(int id, String name, File current_path, File initial_path, int deletion_date) {
        CartConstructor cartConstructor = new CartConstructor(id, name, current_path, initial_path, deletion_date);
        return cartConstructor.returnCart();
    }

    private Cart returnCart() {
        return cart;
    }
}
