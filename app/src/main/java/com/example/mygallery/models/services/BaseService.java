package com.example.mygallery.models.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.mygallery.App;
import com.example.mygallery.database.DatabaseAlbum;
import com.example.mygallery.interfaces.model.DataListener;
import com.example.mygallery.interfaces.model.DataManager;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Album;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;


public abstract class BaseService<T> implements DataManager<T> {

    private final Set<DataListener<T>> listeners;
    protected List<T> list;
    protected Context context;
    protected final DatabaseAlbum databaseManager;
    private final Handler handler;

    public BaseService() {
        this.list = new ArrayList<>();
        this.listeners = new HashSet<>();
        this.context = App.getInstance().getAppContext();
        this.databaseManager = new DatabaseAlbum(context);
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void setData(List<T> item) {
        list = new ArrayList<>(item);
        notifyChanges();
    }

    @Override
    public List<T> getList() {
        return list;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public void addItem(T item) {
        list.add(item);
        notifyChanges();
    }

    @Override
    public void removeItem(int position) {
        list.remove(position);
        updateId(position);
        notifyChanges();
    }

    @Override
    public void clear() {
        list.clear();
        notifyChanges();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    public abstract void getData();

    public void updateData(int id, T newItem) {
        list.set(id, newItem);
        sortListById();
        notifyChanges();
    }

    private void updateId(int id) {
        Executors.newSingleThreadExecutor().submit(() -> {
            for (int i = id; i < list.size(); i++) {
                ((Model) list.get(i)).setId(++i);
            }
            notifyChanges();
        });
    }

    public void addListener(DataListener<T> listener) {
        listeners.add(listener);
        listener.onDataChanged(list);
    }

    public void removeListener(DataListener<T> listener) {
        listeners.remove(listener);
    }

    private void notifyChanges() {
        for (DataListener<T> listener : listeners)
            listener.onDataChanged(list);
    }

    private Runnable startUpdateDatabase(Album data) {
        return () -> {
            databaseManager.insertData(data);
            databaseManager.close();
        };
    }

    private Runnable startUpdateDatabase(List<Album> dataList) {
        return () -> databaseManager.insertData(dataList);
    }

    public void updateDatabase(List<Album> dataList) {
        handler.post(startUpdateDatabase(dataList));
    }

    public void updateDatabase(Album data) {
        handler.post(startUpdateDatabase(data));
    }

    private void sortListById() {
        Executors.newSingleThreadExecutor().submit(() -> {
            list.sort((item1, item2) -> {
                int id1 = ((Model) item1).getId();
                int id2 = ((Model) item2).getId();
                return Integer.compare(id1, id2);
            });
            notifyChanges();
        });
    }

}
