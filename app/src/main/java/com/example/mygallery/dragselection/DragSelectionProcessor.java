package com.example.mygallery.dragselection;

import android.util.SparseBooleanArray;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

public class DragSelectionProcessor implements DragSelectTouchListener.OnAdvancedDragSelectListener {

    private final SparseBooleanArray selectedItems;
    private final SelectedHandler handler;
    private boolean isFirstSelected;

    public DragSelectionProcessor(SelectedHandler handler) {
        this.selectedItems = new SparseBooleanArray();
        this.handler = handler;
        this.handler.getSelection(selectedItems);
    }

    private boolean isChecked(int position) {
        return !selectedItems.get(position, false);
    }

    private void updateSelectedItems(int position) {
        if (isFirstSelected)
            putItem(position);
        else
            deleteItem(position);
    }

    private void putItem(int position) {
        selectedItems.put(position, true);
    }

    private void deleteItem(int position) {
        selectedItems.delete(position);
    }

    @Override
    public void onSelectionStarted(int start) {
        isFirstSelected = isChecked(start);
        updateSelectedItems(start);
        handler.updateSelection(start);
    }

    @Override
    public void onSelectionFinished(int end) {
    }

    @Override
    public void onSelectChange(int start, int end, boolean isSelected) {
        for (int i = start; i <= end; i++) {
            if (isSelected) {
                if (isChecked(i) == isFirstSelected)
                    updateSelectedItems(i);
                else
                    continue;
            } else {
                if (isFirstSelected)
                    deleteItem(i);
                else
                    putItem(i);
            }
            handler.updateSelection(i);
        }
    }

    public interface SelectedHandler {
        void getSelection(SparseBooleanArray selectedItems);

        void updateSelection(int position);
    }
}
