package com.example.mygallery.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class ShareFile {

    final private Context context;

    public ShareFile(Context context){
        this.context = context;
    }

    public void shareFile(Uri fileUri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Просмотр");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Запуск диалогового окна "Поделиться"
        context.startActivity(Intent.createChooser(shareIntent, "Поделиться изображением"));
    }

    public void shareFiles(List<Uri> fileUris) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setType("image/*");
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, new ArrayList<>(fileUris));
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Просмотр");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Запуск диалогового окна "Поделиться"
        context.startActivity(Intent.createChooser(shareIntent, "Поделиться изображениями"));
    }

}
