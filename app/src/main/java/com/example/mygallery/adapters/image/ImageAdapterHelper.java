package com.example.mygallery.adapters.image;

import android.util.SparseBooleanArray;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.adapters.viewholder.ImageViewHolder;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.viewimage.LoadImage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class ImageAdapterHelper<T> extends RecyclerView.Adapter<ImageViewHolder> {

    protected final OnItemClickListener listener;
    protected final OnItemClickListener selectClickListener;
    protected final OnItemClickListener longClickListener;
    protected Mode mode;
    protected List<T> dataList;
    protected SparseBooleanArray selectedItems;

    public ImageAdapterHelper(List<T> dataList, OnItemClickListener listener, OnItemClickListener selectClickListener, OnItemClickListener longClickListener) {
        this.listener = listener;
        this.longClickListener = longClickListener;
        this.selectClickListener = selectClickListener;
        setDataList(dataList);
    }

    public Mode getMode() {
        return mode;
    }

    public void reset() {
        mode = Mode.VIEWING;
        if (selectedItems != null)
            selectedItems.clear();

        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(SparseBooleanArray selectedItems) {
        this.selectedItems = selectedItems;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = new ArrayList<>(dataList);
    }

    public List<T> getDataList() {
        return dataList;
    }

    // Загрузка изображения в ImageView с использованием Glide
    protected void setImage(File imagePath, ImageView imageView) {
        LoadImage.setImage(imagePath, imageView);
    }

    protected void setMode(ImageViewHolder holder, int position) {
        switch (mode) {
            case VIEWING:
                holder.hideSelect();
                setViewingMode(holder, position);
                break;
            case SELECTED:
                holder.showSelect();
                setSelectedMode(holder, position);
                break;
        }
    }

    private void setSelectedMode(ImageViewHolder holder, int position) {

        if (selectedItems != null)
            holder.checkBox.setChecked(selectedItems.get(position, false)); // Установите состояние CheckBox из модели данных

        holder.itemView.setOnClickListener(v -> {
            boolean selectState = !holder.checkBox.isChecked();
            if (selectState)
                selectedItems.put(position, true);
            else
                selectedItems.delete(position);
            setCheckedState(holder, position);
            selectClickListener.onItemClick(position);
        });

        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onItemClick(position);
            return true;
        });


        holder.imageButton.setOnClickListener(v -> listener.onItemClick(position));
    }

    private void setViewingMode(ImageViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
        holder.itemView.setOnLongClickListener(v -> {
            mode = Mode.SELECTED;
            longClickListener.onItemClick(position);
            notifyDataSetChanged();
            return true;
        });
    }

    public void selectedAll() {
        if (selectedItems != null && selectedItems.size() != getItemCount()) {
            for (int i = 0; i < getItemCount(); i++)
                selectedItems.put(i, true);

            notifyDataSetChanged();
        }
    }

    public void clearAll() {
        if (selectedItems != null) {
            for (int i = 0; i < getItemCount(); i++)
                selectedItems.clear();

            notifyDataSetChanged();
        }
    }

    public void setCheckedState(ImageViewHolder holder, int position) {
        holder.checkBox.setChecked(selectedItems.get(position, false)); // Обновите состояние CheckBox
    }

    @NonNull
    @NotNull
    @Override
    public abstract ImageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(@NonNull @NotNull ImageViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public enum Mode {
        SELECTED,
        VIEWING
    }
}
