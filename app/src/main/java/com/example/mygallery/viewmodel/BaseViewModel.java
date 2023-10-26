package com.example.mygallery.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.mygallery.interfaces.model.DataListener;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.services.BaseService;
import com.example.mygallery.multichoice.MultiChoice;
import com.example.mygallery.multichoice.MultiChoiceState;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BaseViewModel<T> extends ViewModel {

    protected final BaseService<T> service;

    private final MutableLiveData<List<T>> _data;
    private final DataListener<T> listener;
    private final MultiChoice<T> multiChoice; // Добавляем MultiChoice
    public LiveData<List<T>> data;

    public BaseViewModel(BaseService<T> service) {
        this.service = service;
        this._data = new MutableLiveData<>();
        this.data = _data;
        this.multiChoice = new MultiChoice<>(); // Создаем объект MultiChoice
        this.listener = _data::postValue;
        loadData();
    }

    public void loadData() {
        service.addListener(listener);
        multiChoice.setItemLiveData(data);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        service.removeListener(listener);
    }

    public void setData(List<T> item) {
        service.setData(item);
    }

    public List<T> getList() {
        return service.getList();
    }

    public int size() {
        return service.size();
    }

    public void addItem(T item) {
        service.addItem(item);
    }

    public void clear() {
        service.clear();
    }

    public void removeItem(int position) {
        service.removeItem(position);
    }

    public boolean isEmpty() {
        return service.isEmpty();
    }

    public T getItem(int position) {
        return service.getList().get(position);
    }

    public void getData() {
        service.getData();
    }

    public void updateData(int id, T item) {
        service.updateData(id, item);
    }

    public void updateDatabase() {
        service.updateDatabase();
    }

    public List<File> getPathList() {
        List<File> paths = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            paths.add(getPath(i));
        }
        return paths;
    }

    public File getPath(int position) {
        return ((Model) getList().get(position)).getPath();
    }

    public int getId(int position) {
        return ((Model) service.getList().get(position)).getId();
    }

    public String getName(int position) {
        return ((Model) service.getList().get(position)).getName();
    }

    // Методы для работы с MultiChoice

    public LiveData<MultiChoiceState<T>> listener() {
        return multiChoice.listener();
    }

    public HashSet<T> getSelectedItems() {
        return multiChoice.getSelectedItems();
    }

    public void toggleSelection(T item) {
        multiChoice.toggle(item);
    }

    public void selectAll() {
        multiChoice.selectAll();
    }

    public void clearAll() {
        multiChoice.clearAll();
    }

    public boolean isChecked(T item) {
        return multiChoice.isChecked(item);
    }

    public int totalCheckedCount() {
        return multiChoice.totalCheckedCount();
    }
}
