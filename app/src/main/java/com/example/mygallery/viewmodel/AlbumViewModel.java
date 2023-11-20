package com.example.mygallery.viewmodel;


import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.services.AlbumService;
import com.example.mygallery.models.services.BaseService;

import java.io.File;

public class AlbumViewModel extends BaseViewModel<Model> {
    public AlbumViewModel(BaseService<Model> service) {
        super(service);
    }

    public int getPathListByPathImage(File pathImage) {
        int position = -1;

        for (int i = 0; i < size(); i++) {
            if (isImage(pathImage, i))
                position = i;
        }

        return position;
    }

    public void findAlbums() {
        ((AlbumService) service).findAlbums();
    }

    private boolean isImage(File pathImage, int position) {
        return getList().get(position).getPath().getAbsolutePath().equals(pathImage.getParent());
    }
}
