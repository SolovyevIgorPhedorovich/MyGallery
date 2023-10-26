package com.example.mygallery.interfaces.model;

import java.util.List;

public interface DataListener<T> {
    void onDataChanged(List<T> listener);
}
