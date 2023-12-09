package com.example.mygallery.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.services.BaseService;
import com.example.mygallery.multichoice.MultiChoice;
import com.example.mygallery.multichoice.MultiChoiceState;

import java.io.File;
import java.util.List;

public class BaseViewModel<T extends Model> extends ViewModel {
    protected final BaseService<T> service;
    private final MutableLiveData<List<T>> _data;
    private final MultiChoice<T> multiChoice;
    public LiveData<List<T>> data;

    public BaseViewModel(BaseService<T> service) {
        this.service = service;
        this._data = new MutableLiveData<>();
        this.data = _data;
        this.multiChoice = new MultiChoice<>();
        loadData();
    }

    // Загружаем данные из сервиса и настраиваем MultiChoice
    private void loadData() {
        service.addListener(_data::postValue); // Обновляем LiveData при изменении данных
        multiChoice.setItemLiveData(data); // Связываем MultiChoice с LiveData
    }

    // Очищаем ресурсы, когда ViewModel больше не используется
    @Override
    protected void onCleared() {
        super.onCleared();
        service.removeListener(_data::postValue); // Удаляем слушатель, чтобы избежать утечек памят
    }

    // Получаем список элементов из сервиса
    public List<T> getList() {
        return service.getList();
    }

    // Получаем размер списка
    public int size() {
        return service.size();
    }

    // Очищаем весь список
    public void clear() {
        service.clear();
    }

    // Удаляем элемент из списка
    public void removeItem(int position) {
        if (totalCheckedCount() == 0) {
            service.removeItem(position);
        } else {
            service.removeItem(getSelectedItems());
        }
    }

    // Проверяем, пуст ли список
    public boolean isEmpty() {
        return service.isEmpty();
    }

    // Получаем элемент из списка по указанному позиции
    public T getItem(int position) {
        return service.getItem(position);
    }

    // Получаем данных из сервиса
    public void getData() {
        service.getData();
    }

    // Обновляем данные элемента в сервисе
    public void updateData(int id, T item) {
        service.updateData(id, item);
    }

    // Обновляем количество фалов в папках
    public void updateDatabase(String curPath, String destPath, int countDuplicateReplace) {
        service.updateDatabase(curPath, destPath, (totalCheckedCount() != 0 ? totalCheckedCount() : 1) - countDuplicateReplace);
    }

    // Получаем путь к файлу элемента по указанному позиции
    public File getPath(int position) {
        return getList().get(position).getPath();
    }

    // Получаем ID элемента по указанному позиции
    public int searchById(int id) {
        return service.searchById(id);
    }

    // Получаем имя элемента по указанному позиции
    public String getName(int position) {
        return service.getList().get(position).getName();
    }

    // Методы для работы с MultiChoice

    // Получаем LiveData для наблюдения за состоянием
    public LiveData<MultiChoiceState<T>> listener() {
        return multiChoice.listener();
    }

    // Получаем список выбранных элементов в MultiChoice
    public List<T> getSelectedItems() {
        return multiChoice.getSelectedItems();
    }

    // Переключаем состояние выбора указанного элемента в MultiChoice
    public void toggleSelection(int position) {
        multiChoice.toggle(getItem(position));
    }

    public void toggleSelection(T item) {
        multiChoice.toggle(item);
    }

    // Выбираем все элементы в MultiChoice
    public void selectAll() {
        multiChoice.selectAll();
    }

    // Очищаем выбор в MultiChoice
    public void clearAll() {
        multiChoice.clearAll();
    }

    // Проверяем, выбран ли элемент по указанному позиции в MultiChoice
    public boolean isChecked(int position) {
        return multiChoice.isChecked(getList().get(position));
    }

    // Получаем общее количество выбранных элементов в MultiChoice
    public int totalCheckedCount() {
        return multiChoice.totalCheckedCount();
    }
}