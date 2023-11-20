package com.example.mygallery.interfaces;

import java.util.List;

public interface OnAdapterInteraction<T> {
    void onSetDataList(List<T> dataList);

    List<T> onGetDataList();
}
