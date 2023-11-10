package com.example.mygallery.editor;

import android.content.Context;
import android.net.Uri;
import android.view.View;

public class ImageEditor extends FileEditor {

    public ImageEditor(Context context, View view, Uri imageUri){
        super(context, Type.IMAGE);
        editIntent.setDataAndType(imageUri,"image/*");
        startEditor(view);
    }

}
