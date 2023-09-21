package com.example.mygallery.adapters;

public interface OnItemClickListener {
    // Интерфейс для обработки кликов
    void onItemClick(int position);

    void onItemClick(String folderName, int position);
}
