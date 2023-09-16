package com.example.mygallery.adapters;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.mygallery.R;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.managers.DataManager;

import java.util.ArrayList;
import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<String> names;
    private List<String> covers;
    private List<Integer> count;
    private OnItemClickListener listener;


    public interface OnItemClickListener{
        void onItemClick(String nameFolder);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public MyItemRecyclerViewAdapter(Context context) {
        this.context = context;
        this.names = new ArrayList<>(DataManager.getInstance(context).getNamesFolders());
        this.covers =new ArrayList<>(DataManager.getInstance(context).getCoversFolders());
        this.count = new ArrayList<>(DataManager.getInstance(context).getCountFiles());
        removeInitialPosition();
    }

    private void removeInitialPosition(){
        if (context instanceof ImageViewActivity){
            ImageViewActivity imageViewActivity = (ImageViewActivity) context;
            int position = imageViewActivity.getPosition();

            names.remove(position);
            covers.remove(position);
            count.remove(position);
        }
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_object, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        loadImageView(holder.imageView, position);

        holder.directoryName.setText(names.get(position));
        holder.countItem.setText(String.valueOf(count.get(position)));

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (listener != null){
                    listener.onItemClick(names.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView directoryName;
        TextView countItem;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.albumImagePreview);
            countItem = itemView.findViewById(R.id.countImages);
            directoryName = itemView.findViewById(R.id.albumDirectoryName);
        }
    }
}