package com.example.mygallery.viewmodel;

import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Album;
import com.example.mygallery.models.services.BaseService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumViewModel extends BaseViewModel<Album> {
    public AlbumViewModel(BaseService<Album> service) {
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

    @Override
    public void updateDatabase() {
        super.updateDatabase();
        getData();
    }

    private boolean isImage(File pathImage, int position) {
        return getList().get(position).path.getAbsolutePath().equals(pathImage.getParent());
    }
}
