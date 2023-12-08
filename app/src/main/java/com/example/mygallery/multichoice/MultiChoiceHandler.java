package com.example.mygallery.multichoice;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface MultiChoiceHandler<T> {
    void setItemLiveData(LiveData<List<T>> liveData);

    LiveData<MultiChoiceState<T>> listener();

    void toggle(T item);

    void selectAll();

    void clearAll();

    void check(T item);

    void clear(T item);
}
