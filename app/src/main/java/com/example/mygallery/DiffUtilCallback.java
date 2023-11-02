package com.example.mygallery;

import androidx.recyclerview.widget.DiffUtil;
import com.example.mygallery.interfaces.model.Model;

import java.util.List;

public class DiffUtilCallback extends DiffUtil.Callback {

    private final List<Model> oldList;
    private final List<Model> newList;

    public DiffUtilCallback(List<Model> oldList, List<Model> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Model oldItem = oldList.get(oldItemPosition);
        Model newItem = newList.get(newItemPosition);
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Model oldItem = oldList.get(oldItemPosition);
        Model newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);
    }
}
