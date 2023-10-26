package com.example.mygallery.adapters.viewholder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;

public class ImageViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public CheckBox checkBox;
    public ImageButton imageButton;

    public ImageViewHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageView);
        checkBox = itemView.findViewById(R.id.checkBox);
        imageButton = itemView.findViewById(R.id.collapse_button);

    }

    public void showSelect() {
        checkBox.setVisibility(View.VISIBLE);
        imageButton.setVisibility(View.VISIBLE);
    }

    public void hideSelect() {
        checkBox.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
    }

}
