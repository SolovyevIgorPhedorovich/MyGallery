package com.example.mygallery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.mygallery.activities.AlbumActivity;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.managers.DataManager;
import com.example.mygallery.R;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ImageViewHolder> {

    private final Context context;
    private DataManager dataManager;
    private List<String> names;
    private List<String> covers;
    private List<Integer> count;
    private final int imageWidth;
    private AlbumAdapter.OnItemClickListener listener;

    public AlbumAdapter(Context context, int imageWidth, AlbumAdapter.OnItemClickListener listener){
        this.context = context;
        this.imageWidth = imageWidth;
        this.listener = listener;
        this.dataManager = DataManager.getInstance();
        this.names = new ArrayList<>(DataManager.getInstance().getNamesFolders());
        this.covers =new ArrayList<>(DataManager.getInstance().getCoversFolders());
        this.count = new ArrayList<>(DataManager.getInstance().getCountFiles());
    }

    public void updateDataAdapter(){
        this.names = new ArrayList<>(DataManager.getInstance().getNamesFolders());
        this.covers =new ArrayList<>(DataManager.getInstance().getCoversFolders());
        this.count = new ArrayList<>(DataManager.getInstance().getCountFiles());
    }

    @NonNull
    @Override
    public AlbumAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_image_view, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = imageWidth;
        layoutParams.height = (int)(imageWidth * 1.5);
        itemView.setLayoutParams(layoutParams);
        return new AlbumAdapter.ImageViewHolder(itemView);
    }

    private void loadImageView(ImageView imageView, int position){
        Glide.with(imageView)
                .load(covers.get(position))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Glide.with(imageView)
                                .load(R.drawable.invalid_image)
                                .into(imageView);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        loadImageView(holder.imageView, position);

        holder.directoryName.setText(names.get(position));
        holder.countItem.setText(String.valueOf(count.get(position)));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(names.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String nameFolder);
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView directoryName;
        TextView countItem;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.albumImagePreview);
            countItem = itemView.findViewById(R.id.countImages);
            directoryName = itemView.findViewById(R.id.albumDirectoryName);
        }
    }

}
