package com.example.mygallery.multichoice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.*;

public class MultiChoice<T> implements MultiChoiceHandler<T>, MultiChoiceState<T> {

    private final Set<T> selectedItems = new LinkedHashSet<>();
    private final MutableLiveData<MultiChoiceState<T>> liveData = new MutableLiveData<>();
    private boolean isAllSelected = false;
    private List<T> items = Collections.emptyList();

    @Override
    public void setItemLiveData(LiveData<List<T>> liveData) {
        liveData.observeForever(newItems -> items = newItems);
    }

    @Override
    public LiveData<MultiChoiceState<T>> listener() {
        return liveData;
    }

    @Override
    public List<T> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }

    @Override
    public void toggle(T item) {
        if (isChecked(item)) {
            clear(item);
        } else {
            check(item);
        }
    }

    @Override
    public void selectAll() {
        selectedItems.addAll(items);
        notifyUpdates();
    }

    @Override
    public void clearAll() {
        selectedItems.clear();
        notifyUpdates();
    }

    @Override
    public void check(T item) {
        if (exists(item)) return;
        selectedItems.add(item);
        notifyUpdates();

    }

    @Override
    public void clear(T item) {
        if (exists(item)) return;
        selectedItems.remove(item);
        notifyUpdates();
    }

    @Override
    public boolean isChecked(T item) {
        return selectedItems.contains(item);
    }

    @Override
    public int totalCheckedCount() {
        return selectedItems.size();
    }

    private boolean exists(T item) {
        return !items.contains(item);
    }

    @Override
    public boolean isAllSelected() {
        return isAllSelected;
    }

    private void notifyUpdates() {
        isAllSelected = totalCheckedCount() == items.size();
        liveData.setValue(this);
    }
}