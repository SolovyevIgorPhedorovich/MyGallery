package com.example.mygallery.adapters.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;
import org.jetbrains.annotations.NotNull;

public class AlbumViewHolder extends RecyclerView.ViewHolder {
    public ImageView folderImageView;
    public TextView fileCountTextView;
    public TextView folderNameTextView;

    public AlbumViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        folderImageView = itemView.findViewById(R.id.album_artwork);
        fileCountTextView = itemView.findViewById(R.id.count_images);
        folderNameTextView = itemView.findViewById(R.id.folder_name);
    }
}
