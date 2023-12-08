package com.example.mygallery.viewmodel;

import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.services.BaseService;
import com.example.mygallery.models.services.ImageService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageViewModel extends BaseViewModel<Model> {
    public ImageViewModel(BaseService<Model> service) {
        super(service);
    }

    public void scanMediaAlbum(File path) {
        ((ImageService) service).scanMediaAlbum(path);
    }

    public void moveToCart(int position) {
        if (totalCheckedCount() == 0) {
            ((ImageService) service).moveToCart(position);
        } else {
            ((ImageService) service).moveToCart(new ArrayList<>(getSelectedItems()));
        }
    }

    public void updateFavorites(int position, List<File> newPathList) {
        if (totalCheckedCount() == 0) {
            ((ImageService) service).updateFavorites(position, newPathList.get(0));
        } else {
            ((ImageService) service).updateFavorites(new ArrayList<>(getSelectedItems()), newPathList);
        }
    }

    public void setArtwork(File path) {
        ((ImageService) service).setArtwork(path);
    }

    public void setFavorites(Model image) {
        ((ImageService) service).setFavorites(image);
    }
}
