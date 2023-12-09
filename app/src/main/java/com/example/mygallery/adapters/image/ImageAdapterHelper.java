package com.example.mygallery.adapters.image;

import android.graphics.Point;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.adapters.viewholder.ImageViewHolder;
import com.example.mygallery.interfaces.OnAdapterInteraction;
import com.example.mygallery.interfaces.OnCheckedIsChoice;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.viewimage.LoadImage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class ImageAdapterHelper<T> extends RecyclerView.Adapter<ImageViewHolder> implements OnAdapterInteraction<T> {

    // Обработчики событий
    protected final OnItemClickListener listener;
    protected final OnItemClickListener selectClickListener;
    protected final OnItemClickListener longClickListener;
    protected final OnCheckedIsChoice checked;

    // Режим работы адаптера
    protected Mode mode;
    protected List<T> dataList;

    // Состояние выбранных элементов
    protected SparseBooleanArray selectedItems;

    // Конструктор
    public ImageAdapterHelper(List<T> dataList, OnItemClickListener listener, OnItemClickListener selectClickListener, OnItemClickListener longClickListener, OnCheckedIsChoice checked) {
        this.listener = listener;
        this.longClickListener = longClickListener;
        this.selectClickListener = selectClickListener;
        this.checked = checked;
        onSetDataList(dataList);
    }

    // Получение текущего режима работы адаптера
    public Mode getMode() {
        return mode;
    }

    // Сброс состояния адаптера
    public void reset() {
        mode = Mode.VIEWING;
        clearSelectedItems();
        notifyDataSetChanged();
    }

    // Получение выбранных элементов
    public SparseBooleanArray getSelectedItems() {
        return selectedItems;
    }

    // Установка выбранных элементов
    public void setSelectedItems(SparseBooleanArray selectedItems) {
        this.selectedItems = selectedItems;
    }

    // Установка списка данных
    @Override
    public void onSetDataList(List<T> dataList) {
        this.dataList = new ArrayList<>(dataList);
    }

    // Получение списка данных
    @Override
    public List<T> onGetDataList() {
        return dataList;
    }

    // Загрузка изображения в ImageView с использованием Glide
    protected void setImage(File imagePath, ImageView imageView, Point imageSize) {
        LoadImage.setImage(imagePath, imageView, imageSize);
    }

    // Установка режима для ViewHolder
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

    // Установка режима SELECTED для ViewHolder
    private void setSelectedMode(ImageViewHolder holder, int position) {

        if (selectedItems != null) {
            holder.checkBox.setChecked(selectedItems.get(position, false)); // Установите состояние CheckBox из модели данных
        }
        holder.itemView.setOnClickListener(v -> selectClickListener.onItemClick(position));
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onItemClick(position);
            return true;
        });

        holder.imageButton.setOnClickListener(v -> listener.onItemClick(holder.getAbsoluteAdapterPosition()));
    }

    // Установка режима VIEWING для ViewHolder
    private void setViewingMode(ImageViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> listener.onItemClick(holder.getAbsoluteAdapterPosition()));
        holder.itemView.setOnLongClickListener(v -> {
            mode = Mode.SELECTED;
            longClickListener.onItemClick(position);
            notifyDataSetChanged();
            return true;
        });
    }

    // Выбор всех элементов
    public void selectedAll() {
        if (selectedItems != null && selectedItems.size() != getItemCount()) {
            for (int i = 0; i < getItemCount(); i++)
                selectedItems.put(i, true);

            notifyDataSetChanged();
        }
    }

    // Снятие выбора со всех элементов
    public void clearAll() {
        clearSelectedItems();
        notifyDataSetChanged();
    }

    // Очистка выбранных элементов
    private void clearSelectedItems() {
        if (selectedItems != null) {
            selectedItems.clear();
        }
    }

    // Обновление выбранных элементов при изменении данных
    public void updateSelectedItems(List<T> selectedItemList) {
        int oldSize = selectedItems.size();
        int newSize = selectedItemList.size();

        if (newSize < oldSize) {
            updateForLessItems(selectedItemList, oldSize);
        } else if (newSize > oldSize) {
            updateForMoreItems(selectedItemList, newSize);
        }
    }

    // Обновление для уменьшения количества выбранных элементов
    private void updateForLessItems(List<T> selectedItemList, int oldSize) {
        for (int a = 0, i = oldSize - 1; a <= i; a++, i--) {
            int position1 = selectedItems.keyAt(a);
            int position2 = selectedItems.keyAt(i);

            if (!selectedItemList.contains(dataList.get(position1))) {
                notifyItemChanged(position1);
                break;
            } else if (!selectedItemList.contains(dataList.get(position2))) {
                notifyItemChanged(position2);
                break;
            }
        }
    }

    // Обновление для увеличения количества выбранных элементов
    private void updateForMoreItems(List<T> selectedItemList, int newSize) {
        for (int i = 0; i < getItemCount(); i++) {
            if (dataList.get(i).equals(selectedItemList.get(newSize - 1))) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    // Обновление состояния CheckBox
    protected void updateSelectedItems(int position) {
        boolean currentValue = selectedItems.get(position, false);
        boolean actualityValue = checked.onChecked(position);
        if (actualityValue && !currentValue) {
            selectedItems.put(position, true);
        } else if (!actualityValue && currentValue) {
            selectedItems.delete(position);
        }
    }

    // Установка состояния CheckBox для ViewHolder
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

    // Перечисление режимов работы адаптера
    public enum Mode {
        SELECTED,
        VIEWING
    }
}