package com.example.mygallery.multichoice;

import android.graphics.Canvas;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.adapters.viewholder.ImageViewHolder;

public class LongPressItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private final RecyclerView recyclerView;
    private final OnItemLongClickListener onItemLongClickListener;

    public LongPressItemTouchHelper(RecyclerView recyclerView, OnItemLongClickListener listener) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT); // Настройте флаги свайпа на нулевые, чтобы избежать конфликтов
        this.recyclerView = recyclerView;
        this.onItemLongClickListener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // Реализуйте логику перетаскивания элементов, если необходимо
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // Реализуйте логику свайпа элементов, если необходимо
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder.itemView.setPressed(true); // Выделение элемента при начале перетаскивания
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setPressed(false); // Сброс выделения после завершения перетаскивания
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        // Реализуйте логику рисования поверх элементов, если необходимо
    }


/*
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState, int position) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ImageViewHolder) {
                YourViewHolder yourViewHolder = (ImageViewHolder) viewHolder;
                onItemLongClickListener.onItemLongClick(yourViewHolder.getAdapterPosition());
            }
        }
        super.onSelectedChanged(viewHolder, actionState, position);
    }*/

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}

