package com.example.mygallery.adapters.viewholder;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;
import com.github.chrisbanes.photoview.PhotoView;

public class ImagePagerViewHolder extends RecyclerView.ViewHolder {
    public PhotoView imageView;
    private final float[] scaleHorizontal = new float[3];
    private final float[] scaleVertical = new float[3];
    public View itemView;
    public boolean isRotated = false;

    public ImagePagerViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView.getRootView();
        imageView = itemView.findViewById(R.id.image_view);
    }

    public void setScaleHorizontal(float minScale, float midScale, float maxScale) {
        scaleHorizontal[0] = minScale;
        scaleHorizontal[1] = midScale;
        scaleHorizontal[2] = maxScale;
    }

    public void setScaleVertical(float minScale, float midScale, float maxScale) {
        scaleVertical[0] = minScale;
        scaleVertical[1] = midScale;
        scaleVertical[2] = maxScale;
    }

    public float[] getScaleHorizontal() {
        return scaleHorizontal;
    }

    public float[] getScaleVertical() {
        return scaleVertical;
    }
}
