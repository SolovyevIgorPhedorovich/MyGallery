package com.example.mygallery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.mygallery.DataManager;
import com.example.mygallery.R;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ImageViewHolder> {

    private final Context context;
    private DataManager dataManager;
    private TextView directoryName;
    private TextView countItem;
    private final int imageWidth;
    private AlbumAdapter.OnItemClickListener listener;

    public AlbumAdapter(Context context, int imageWidth, AlbumAdapter.OnItemClickListener listener){
        this.context = context;
        this.imageWidth = imageWidth;
        this.listener = listener;
        dataManager = DataManager.getInstance();
    }

    @NonNull
    @Override
    public AlbumAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_image_view, parent, false);
        countItem = itemView.findViewById(R.id.countImages);
        directoryName = itemView.findViewById(R.id.albumDirectoryName);
        return new AlbumAdapter.ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(holder.imageView)
                .load(dataManager.getCoversFolders().get(position))
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                .into(holder.imageView);

        directoryName.setText(dataManager.getNamesFolders().get(position));
        countItem.setText(String.valueOf(dataManager.getCountFiles().get(position)));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataManager.getNamesFolders().size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.albumImagePreview);
        }
    }

}
