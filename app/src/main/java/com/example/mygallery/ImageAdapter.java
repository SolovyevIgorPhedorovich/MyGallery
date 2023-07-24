package com.example.mygallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<String> imagePaths;
    private int imageSize;
    private OnItemClickListener listener;

    public ImageAdapter(Context context, List<String> imagePaths, int imageSize, OnItemClickListener listener){
        this.context = context;
        this.imagePaths = imagePaths;
        this.imageSize = imageSize;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = imageSize;
        layoutParams.height = imageSize;
        itemView.setLayoutParams(layoutParams);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String imagePath = imagePaths.get(position);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        options.inSampleSize = calculetSize(options);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        holder.imageView.setImageBitmap(bitmap);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    listener.onItemClick(position);
                }
        });
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
    private int calculetSize(BitmapFactory.Options options){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > imageSize || width > imageSize){
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > imageSize && (halfWidth / inSampleSize) > imageSize){
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
