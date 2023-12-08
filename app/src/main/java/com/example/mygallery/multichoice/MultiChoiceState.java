package com.example.mygallery.multichoice;

import java.util.List;

public interface MultiChoiceState<T> {

    List<T> getSelectedItems();

    boolean isChecked(T item);

    boolean isAllSelected();

    int totalCheckedCount();
}
