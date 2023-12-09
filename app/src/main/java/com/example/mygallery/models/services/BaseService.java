package com.example.mygallery.models.services;

import android.content.Context;
import com.example.mygallery.App;
import com.example.mygallery.database.DatabaseAlbum;
import com.example.mygallery.interfaces.model.DataListener;
import com.example.mygallery.interfaces.model.DataManager;
import com.example.mygallery.interfaces.model.Model;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract class BaseService<T extends Model> implements DataManager<T> {

    private final Set<DataListener<T>> listeners;
    protected final DatabaseAlbum databaseManager;
    protected List<T> list;
    protected Context context;

    public BaseService() {
        this.list = new ArrayList<>();
        this.listeners = new HashSet<>();
        this.context = App.getInstance().getAppContext();
        this.databaseManager = new DatabaseAlbum(context);
    }

    protected static ExecutorService executeInSingleThread(Runnable task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
        executor.shutdown();
        return executor;
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
    public void setData(List<T> items) {
        list = new ArrayList<>(items);
        notifyChanges();
    }

    @Override
    public void removeItem(int position) {
        list.remove(position);
        updateId(position);
        notifyChanges();
    }

    @Override
    public void removeItem(List<T> selectItemList) {
        int min = list.size();
        for (T item : selectItemList) {
            int id = item.getId();
            list.remove(searchById(id));
            min = Math.min(id, min);
        }
        updateId(min);
        notifyChanges();
    }

    public int searchById(int id) {
        int left = 0;
        int right = list.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int currentId = list.get(mid).getId();

            if (currentId == id) {
                return mid; // Элемент найден, возвращаем позицию
            } else if (currentId < id) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1; // Элемент не найден
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
        list.set(id - 1, newItem);
        //sortListById(); //TODO: заменить на сортировку по параметру из SharedPreferences
        notifyChanges();
    }

    @Override
    public T getItem(int position) {
        return list.get(position);
    }

    public void addListener(DataListener<T> listener) {
        listeners.add(listener);
        listener.onDataChanged(list);
    }

    public void removeListener(DataListener<T> listener) {
        listeners.remove(listener);
    }

    private void updateId(int id) {
        executeInSingleThread(() -> {
            for (int i = id; i < list.size(); i++) {
                list.get(i).setId(++i);
            }
            notifyChanges();
        });
    }

    private Runnable startUpdateDatabase(String curPath, String destPath, int count) {
        return () -> databaseManager.updateData(curPath, destPath, count);
    }

    private void notifyChanges() {
        for (DataListener<T> listener : listeners) {
            listener.onDataChanged(list);
        }
    }

    public void updateDatabase(String curPath, String destPath, int count) {
        executeInSingleThread(startUpdateDatabase(curPath, destPath, count));
    }

    private void sortListById() {
        executeInSingleThread(() -> {
            list.sort(Comparator.comparingInt(Model::getId));
            notifyChanges();
        });
    }
}