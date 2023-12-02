package com.example.mygallery.interfaces.model;

import java.util.List;

public interface DataManager<T> {
    void setData(List<T> item);

    List<T> getList();

    int size();

    void addItem(T item);

    void removeItem(int position);

    void removeItem(List<T> positionList);

    void clear();

    boolean isEmpty();

}
