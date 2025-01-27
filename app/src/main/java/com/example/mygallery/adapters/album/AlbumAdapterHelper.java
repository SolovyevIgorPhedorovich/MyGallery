package com.example.mygallery.adapters.album;

import android.graphics.Point;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.adapters.viewholder.AlbumViewHolder;
import com.example.mygallery.interfaces.OnAdapterInteraction;
import com.example.mygallery.interfaces.OnCheckedIsChoice;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.viewimage.LoadImage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AlbumAdapterHelper<T> extends RecyclerView.Adapter<AlbumViewHolder> implements OnAdapterInteraction<T> {

    protected final OnItemClickListener listener;
    protected List<T> dataList;
    protected OnCheckedIsChoice checked;

    // Состояние выбранных элементов
    protected SparseBooleanArray selectedItems = new SparseBooleanArray();
    protected Mode mode = Mode.VIEWING;

    public AlbumAdapterHelper(List<T> dataList, OnItemClickListener listener, OnCheckedIsChoice checked) {
        this.listener = listener;
        this.checked = checked;
        onSetDataList(dataList);
    }

    public AlbumAdapterHelper(List<T> dataList, OnItemClickListener listener) {
        this.listener = listener;
        onSetDataList(dataList);
    }

    @Override
    public void onSetDataList(List<T> dataList) {
        this.dataList = new ArrayList<>(dataList);
    }

    @Override
    public List<T> onGetDataList() {
        return this.dataList;
    }

    @NonNull
    @NotNull
    @Override
    public abstract AlbumViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull @NotNull AlbumViewHolder holder, int position) {
        // Устанавливаем состояние для ViewHolder в зависимости от текущего режима
        setMode(holder, position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // Установка режима для ViewHolder
    protected void setMode(AlbumViewHolder holder, int position) {
        switch (mode) {
            case VIEWING:
                holder.itemView.setSelected(false);
                setViewingMode(holder, position);
                break;
            case SELECTED:
                holder.itemView.setSelected(selectedItems.get(position, false));
                setSelectedMode(holder, position);
                break;
        }
    }

    // Режим "выделение"
    private void setSelectedMode(AlbumViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> toggleSelection(position));
        holder.itemView.setOnLongClickListener(v -> {
            mode = Mode.VIEWING;
            clearAllSelections();
            notifyDataSetChanged();
            return true;
        });
    }

    // Режим "просмотр"
    private void setViewingMode(AlbumViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
        holder.itemView.setOnLongClickListener(v -> {
            mode = Mode.SELECTED;
            toggleSelection(position);
            notifyDataSetChanged();
            return true;
        });
    }

    // Переключение состояния элемента
    private void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    // Снять выделение со всех элементов
    public void clearAllSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    // Получить список выделенных элементов
    public List<T> getSelectedItems() {
        List<T> selectedData = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
            int position = selectedItems.keyAt(i);
            selectedData.add(dataList.get(position));
        }
        return selectedData;
    }

    // Проверить, выделен ли элемент
    public boolean isItemSelected(int position) {
        return selectedItems.get(position, false);
    }

    // Перечисление режимов работы
    public enum Mode {
        VIEWING,
        SELECTED
    }
}

