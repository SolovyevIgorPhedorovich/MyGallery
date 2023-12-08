package com.example.mygallery.viewmodel;


import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.services.AlbumService;
import com.example.mygallery.models.services.BaseService;

import java.io.File;

public class AlbumViewModel extends BaseViewModel<Model> {
    public AlbumViewModel(BaseService<Model> service) {
        super(service);
    }

    // Метод для поиска индекса элемента в списке по пути изображения
    public int getPathListByPathImage(File pathImage) {
        // Перебор элементов списка
        for (int i = 0; i < size(); i++) {
            // Если текущий элемент является корневой директорией элемента, то запоминаем его индекс
            if (isRootDirectoryForImage(pathImage, i))
                return i;
        }

        // Возвращаем индекс элемента в списке или -1, если не найден
        return -1;
    }

    // Метод для вызова поиска альбомов
    public void findAlbums() {
        ((AlbumService) service).findAlbums();
    }

    // Вспомогательный метод для проверки, корневой директории элемента
    private boolean isRootDirectoryForImage(File pathImage, int position) {
        // Сравниваем абсолютные пути элемента и указанного файла
        return getList().get(position).getPath().getAbsolutePath().equals(pathImage.getParent());
    }
}
