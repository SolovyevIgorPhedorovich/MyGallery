package com.example.mygallery;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.interfaces.model.Model;

import java.util.List;

public class DiffUtilCallback<T extends Model> extends DiffUtil.Callback {

    private final List<T> oldList;
    private final List<T> newList;

    // Конструктор для инициализации старого и нового списков
    public DiffUtilCallback(List<T> oldList, List<T> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    // Возвращает размер старого списка
    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    // Возвращает размер нового списка
    @Override
    public int getNewListSize() {
        return newList.size();
    }

    // Проверяет, являются ли элементы с указанными позициями одними и теми же элементами на основе их идентификаторов
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        T oldItem = oldList.get(oldItemPosition);
        T newItem = newList.get(newItemPosition);
        return oldItem.getId() == newItem.getId();
    }

    // Проверяет, являются ли содержимое элементов с указанными позициями одинаковыми
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T oldItem = oldList.get(oldItemPosition);
        T newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);
    }

    // Запускает расчет разницы и отправляет обновления в предоставленный адаптер
    public void start(RecyclerView.Adapter<?> adapter) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(this);
        diffResult.dispatchUpdatesTo(adapter);
    }
}