package com.example.mygallery.adapters.viewholder;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;
import com.github.chrisbanes.photoview.PhotoView;
import org.jetbrains.annotations.NotNull;

public class ImagePagerViewHolder extends RecyclerView.ViewHolder {
    public PhotoView imageView;
    public float rotationAngle;

    public ImagePagerViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image_view);
        rotationAngle = 0f;
    }
}
