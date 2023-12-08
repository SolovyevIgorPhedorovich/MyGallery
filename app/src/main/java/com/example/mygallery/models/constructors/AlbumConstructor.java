package com.example.mygallery.models.constructors;

import com.example.mygallery.models.Album;

import java.io.File;

public class AlbumConstructor {

    private final Album album;

    private AlbumConstructor(int id, String name, File path, int count, File artwork) {
        album = new Album();
        album.id = id;
        album.name = name;
        album.path = path;
        album.count = count;
        album.artwork = artwork;
    }

    public static Album create(int id, String name, File path, int count, File artwork) {
        AlbumConstructor albumConstructor = new AlbumConstructor(id, name, path, count, artwork);
        return albumConstructor.getAlbum();
    }

    private Album getAlbum() {
        return album;
    }

}
