package com.example.mygallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ImageViewHolder> {

    private Context context;
    private List<String> imageNameAlbum;
    private List<String> imagePreviewPaths;
    private List<Integer> countItemDirectory;
    private TextView directoryName;
    private TextView countItem;
    private int imageWidth;
    private AlbumAdapter.OnItemClickListener listener;

    public AlbumAdapter(Context context, List<String> imageNameAlbum, List<String> imagePreviewPaths, List<Integer> countItemDirectory, int imageWidth, AlbumAdapter.OnItemClickListener listener){
        this.context = context;
        this.imageNameAlbum = imageNameAlbum;
        this.imagePreviewPaths = imagePreviewPaths;
        this.countItemDirectory = countItemDirectory;
        this.imageWidth = imageWidth;
        this.listener = listener;
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
                .load(imagePreviewPaths.get(position))
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                .into(holder.imageView);

        directoryName.setText(imageNameAlbum.get(position));
        countItem.setText(String.valueOf(countItemDirectory.get(position)));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageNameAlbum.size();
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
