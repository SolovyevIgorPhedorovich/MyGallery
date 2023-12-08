package com.example.mygallery.models.constructors;

import com.example.mygallery.models.Cart;

import java.io.File;

public class CartConstructor {

    private final Cart cart;

    private CartConstructor(int id, String name, File currentPath, File initialPath, int deletionDate) {
        cart = new Cart();
        cart.id = id;
        cart.name = name;
        cart.currentPath = currentPath;
        cart.initialPath = initialPath;
        cart.deletionDate = deletionDate;
    }

    public static Cart create(int id, String name, File currentPath, File initialPath, int deletion_date) {
        CartConstructor cartConstructor = new CartConstructor(id, name, currentPath, initialPath, deletion_date);
        return cartConstructor.returnCart();
    }

    private Cart returnCart() {
        return cart;
    }
}
