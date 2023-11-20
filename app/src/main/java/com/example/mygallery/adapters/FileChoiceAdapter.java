package com.example.mygallery.adapters;

import android.content.Context;
import com.example.mygallery.adapters.image.ImageAdapter;
import com.example.mygallery.interfaces.OnCheckedIsChoice;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.interfaces.model.Model;

import java.util.List;

public class FileChoiceAdapter extends ImageAdapter<Model> {

    public FileChoiceAdapter(Context context, List<Model> pathImages, int spanCount, OnItemClickListener listener, OnItemClickListener selectClickListener, OnItemClickListener longClickListener, OnCheckedIsChoice checked) {
        super(context, pathImages, spanCount, listener, selectClickListener, longClickListener, checked);
        mode = Mode.SELECTED;
    }
}
