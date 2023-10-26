package com.example.mygallery.multichoice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.*;

public class MultiChoice<T> implements MultiChoiceHandler<T>, MultiChoiceState<T> {

    private final HashSet<T> selectedItems = new HashSet<>();
    private final MutableLiveData<MultiChoiceState<T>> liveData = new MutableLiveData<>();
    private boolean isAllSelected = false;
    private List<T> items = Collections.emptyList();

    @Override
    public void setItemLiveData(LiveData<List<T>> liveData) {
        liveData.observeForever(new androidx.lifecycle.Observer<List<T>>() {
            @Override
            public void onChanged(List<T> newItems) {
                items = newItems;
                removedDeletedFile(newItems);
                notifyUpdates();
            }
        });
    }

    @Override
    public LiveData<MultiChoiceState<T>> listener() {
        return liveData;
    }

    public HashSet<T> getSelectedItems() {
        return selectedItems;
    }

    @Override
    public void toggle(T item) {
        if (isChecked(item))
            clear(item);
        else
            check(item);
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
        if (!exists(item)) return;
        selectedItems.add(item);
        notifyUpdates();

    }

    @Override
    public void clear(T item) {
        if (!exists(item)) return;
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
        for (T image : items)
            if (item.equals(image))
                return true;

        return false;
    }

    private void removedDeletedFile(List<T> files) {
        List<T> selectedItemsCopy = new ArrayList<>(selectedItems);

        selectedItemsCopy.removeAll(new HashSet<>(files));

        selectedItems.clear();
        selectedItems.addAll(selectedItemsCopy);
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
