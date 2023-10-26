package com.example.mygallery.models.services;

import android.content.Context;
import com.example.mygallery.App;
import com.example.mygallery.interfaces.model.DataListener;
import com.example.mygallery.interfaces.model.DataManager;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.scaning.ScanMemoryRunnable;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public abstract class BaseService<T> implements DataManager<T> {

    private final Set<DataListener<T>> listeners;
    protected List<T> list;
    protected Context context;

    public BaseService() {
        this.list = new ArrayList<>();
        this.listeners = new HashSet<>();
        this.context = App.getInstance().getAppContext();
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

    public void updateDatabase() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.submit(new ScanMemoryRunnable(context));
        executor.shutdown();
    }

    private void sortListById() {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                list.sort((item1, item2) -> {
                    int id1 = ((Model) item1).getId();
                    int id2 = ((Model) item2).getId();
                    return Integer.compare(id1, id2);
                });
                notifyChanges();
            }
        });
    }

}
