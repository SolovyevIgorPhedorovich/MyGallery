package com.example.mygallery.multichoice;

public interface MultiChoiceState<T> {

    boolean isChecked(T item);

    boolean isAllSelected();

    int totalCheckedCount();
}
