package com.example.mygallery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.mygallery.activities.AlbumActivity;
import com.example.mygallery.activities.MainActivity;
import com.example.mygallery.managers.DataManager;
import com.example.mygallery.R;

import java.io.File;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<File> imagePaths;
    private int imageSize;
    private OnItemClickListener listener;

    public ImageAdapter(Context context, List<File> imagePaths, OnItemClickListener listener){
        this.context = context;
        this.imagePaths = imagePaths;
        this.imageSize = setSizeImage();
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
        Glide.with(holder.imageView)
                .load(imagePaths.get(position))
                .apply(RequestOptions.overrideOf(imageSize))
                .into(holder.imageView);

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

    private int setSizeImage() {
        //Получение ширины экрна
        Display display;
        if (context instanceof AlbumActivity)
            display = ((AlbumActivity)context).getWindowManager().getDefaultDisplay();
        else
            display = ((MainActivity)context).getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        //Получение ширины отступа
        //int imageMargin = ((MainActivity)context).getDimensionPixelSize(R.dimen.image_layout_margin);

        //Вычисление ширины для отображения изображения в сетке
        return imageSize = screenWidth / 4;
    }
}
