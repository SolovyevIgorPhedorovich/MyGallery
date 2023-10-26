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
        returnAlbum();
    }

    public static Album initialized(int id, String name, File path, int count, File artwork) {
        AlbumConstructor albumConstructor = new AlbumConstructor(id, name, path, count, artwork);
        return albumConstructor.returnAlbum();
    }

    private Album returnAlbum() {
        return album;
    }

}
