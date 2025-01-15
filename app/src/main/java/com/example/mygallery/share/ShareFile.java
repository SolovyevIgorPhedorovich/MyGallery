package com.example.mygallery.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ShareFile {

    private Context context;

    public ShareFile(Context context){
        this.context = context;
    }

    public void shareFile(Uri filePath) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, filePath);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Просмотр");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Запуск диалогового окна "Поделиться"
        context.startActivity(Intent.createChooser(shareIntent, "Поделиться изображением"));
    }

}
