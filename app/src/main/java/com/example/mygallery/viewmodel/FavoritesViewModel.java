package com.example.mygallery.viewmodel;

import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.services.BaseService;

public class FavoritesViewModel extends BaseViewModel<Model> {
    public FavoritesViewModel(BaseService<Model> service) {
        super(service);
    }
}
