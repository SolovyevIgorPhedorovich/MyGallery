package com.example.mygallery.viewmodel;

import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Cart;
import com.example.mygallery.models.services.BaseService;

public class CartViewModel extends BaseViewModel<Model> {
    public CartViewModel(BaseService<Model> service) {
        super(service);
    }


}
