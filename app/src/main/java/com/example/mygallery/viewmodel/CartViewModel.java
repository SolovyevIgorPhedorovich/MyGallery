package com.example.mygallery.viewmodel;

import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.services.BaseService;
import com.example.mygallery.models.services.CartService;

import java.util.ArrayList;

public class CartViewModel extends BaseViewModel<Model> {
    public CartViewModel(BaseService<Model> service) {
        super(service);
    }

    public void updateDatabase(int position) {
        if (totalCheckedCount() != 0) {
            ((CartService) service).updateDatabase(new ArrayList<>(getSelectedItems()));

        } else if (position != -1) {
            ((CartService) service).updateDatabase(position);
        }
    }

}
