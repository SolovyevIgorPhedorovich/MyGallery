package com.example.mygallery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mygallery.DataManager;
import com.example.mygallery.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private DataManager dataManager;
    private int imageSize;
    private OnItemClickListener listener;

    public ImageAdapter(Context context, int imageSize, OnItemClickListener listener){
        this.context = context;
        this.dataManager = DataManager.getInstance();
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
        Glide.with(holder.imageView)
                .load(dataManager.getPathsFiles().get(position))
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
        return dataManager.getPathsFiles().size();
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
}
