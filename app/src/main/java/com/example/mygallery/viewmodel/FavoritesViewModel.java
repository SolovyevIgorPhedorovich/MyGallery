package com.example.mygallery.viewmodel;

import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.services.BaseService;
import com.example.mygallery.models.services.FavoritesService;

public class FavoritesViewModel extends BaseViewModel<Model> {
    public FavoritesViewModel(BaseService<Model> service) {
        super(service);
    }

    public void updateDatabase(int position) {
        ((FavoritesService) service).updateDatabase(position);
    }
}
