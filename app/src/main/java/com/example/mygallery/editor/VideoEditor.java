package com.example.mygallery.editor;

import android.content.Context;
import android.net.Uri;
import android.view.View;

public class VideoEditor extends FileEditor {

    public VideoEditor(Context context, View view, Uri videoUri) {
        super(context, Type.VIDEO);
        editIntent.setDataAndType(videoUri, "video/*");
        startEditor(view);
    }

}
