package com.example.mygallery.viewmodel;

import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Image;
import com.example.mygallery.models.services.BaseService;
import com.example.mygallery.models.services.ImageService;

import java.io.File;

public class ImageViewModel extends BaseViewModel<Model> {
    public ImageViewModel(BaseService<Model> service) {
        super(service);
    }

    public void scanMediaAlbum(File path) {
        ((ImageService) service).scanMediaAlbum(path);
    }

    public void setFavorites(Image image) {
        ((ImageService) service).setFavorites(image);
    }
}
