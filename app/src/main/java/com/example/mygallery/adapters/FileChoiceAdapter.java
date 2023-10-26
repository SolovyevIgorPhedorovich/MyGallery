package com.example.mygallery.adapters;

import android.content.Context;
import com.example.mygallery.adapters.image.ImageAdapter;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.interfaces.model.Model;

import java.util.List;

public class FileChoiceAdapter extends ImageAdapter<Model> {

    public FileChoiceAdapter(Context context, List<Model> pathImages, OnItemClickListener listener, OnItemClickListener selectClickListener, OnItemClickListener longClickListener) {
        super(context, pathImages, listener, selectClickListener, longClickListener);
        mode = Mode.SELECTED;
    }
}
