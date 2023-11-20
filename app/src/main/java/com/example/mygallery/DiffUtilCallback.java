package com.example.mygallery;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.interfaces.model.Model;

import java.util.List;

public class DiffUtilCallback<T> extends DiffUtil.Callback {

    private final List<T> oldList;
    private final List<T> newList;

    public DiffUtilCallback(List<T> oldList, List<T> newList) {
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
        Model oldItem = (Model) oldList.get(oldItemPosition);
        Model newItem = (Model) newList.get(newItemPosition);
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T oldItem = oldList.get(oldItemPosition);
        T newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);
    }

    public void start(RecyclerView.Adapter<?> adapter) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(this);
        diffResult.dispatchUpdatesTo(adapter);
    }
}
