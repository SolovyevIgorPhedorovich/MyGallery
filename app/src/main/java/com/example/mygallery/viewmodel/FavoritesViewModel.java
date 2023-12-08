package com.example.mygallery.viewmodel;

import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.services.BaseService;
import com.example.mygallery.models.services.FavoritesService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FavoritesViewModel extends BaseViewModel<Model> {
    public FavoritesViewModel(BaseService<Model> service) {
        super(service);
    }

    public void moveToCart(int position) {
        if (totalCheckedCount() == 0) {
            ((FavoritesService) service).moveToCart(position);
        } else {
            ((FavoritesService) service).moveToCart(new ArrayList<>(getSelectedItems()));
        }
    }

    public void updateFavorites(int position, List<File> newPathList) {
        if (totalCheckedCount() == 0) {
            ((FavoritesService) service).updateFavorites(position, newPathList.get(0));
        } else {
            ((FavoritesService) service).updateFavorites(new ArrayList<>(getSelectedItems()), newPathList);
        }
    }

    public void updateDatabase(int position) {
        ((FavoritesService) service).updateDatabase(position);
    }
}
