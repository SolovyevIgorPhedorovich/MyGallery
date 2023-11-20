package com.example.mygallery.adapters;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;
import com.example.mygallery.interfaces.OnButtonClickListener;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.viewimage.LoadImage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListSelectedAdapter extends RecyclerView.Adapter<ListSelectedAdapter.ImageViewHolder> {
    private final int imageSize;
    private final OnButtonClickListener listener;
    private List<Model> imageList;

    public ListSelectedAdapter(Context context, List<Model> imageList, OnButtonClickListener listener) {
        setList(imageList);
        this.listener = listener;
        this.imageSize = context.getResources().getDimensionPixelSize(R.dimen.size_select_image);
    }

    public List<Model> getList() {
        return this.imageList;
    }

    public void setList(List<Model> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @NotNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_list_selected, parent, false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ImageViewHolder holder, int position) {
        LoadImage.setImage(imageList.get(position).getPath(), holder.imageView, new Point(imageSize, imageSize));

        holder.imageButton.setOnClickListener(v -> listener.onGetItem(imageList.get(holder.getBindingAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageButton imageButton;

        public ImageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.button_delete_on_selected);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
