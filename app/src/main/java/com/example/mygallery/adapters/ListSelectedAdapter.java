package com.example.mygallery.adapters;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;
import com.example.mygallery.databinding.ViewHolderListSelectedBinding;
import com.example.mygallery.interfaces.OnAdapterInteraction;
import com.example.mygallery.interfaces.OnButtonClickListener;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.viewimage.LoadImage;

import java.util.List;

public class ListSelectedAdapter extends RecyclerView.Adapter<ListSelectedAdapter.ImageViewHolder> implements OnAdapterInteraction<Model> {

    private final int imageSize;
    private final OnButtonClickListener listener;
    private List<Model> imageList;

    public ListSelectedAdapter(Context context, List<Model> imageList, OnButtonClickListener listener) {
        onSetDataList(imageList);
        this.listener = listener;
        this.imageSize = context.getResources().getDimensionPixelSize(R.dimen.size_select_image);
    }

    @Override
    public List<Model> onGetDataList() {
        return this.imageList;
    }

    @Override
    public void onSetDataList(List<Model> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewHolderListSelectedBinding binding = ViewHolderListSelectedBinding.inflate(inflater, parent, false);
        return new ImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Model currentItem = imageList.get(position);
        LoadImage.setImage(currentItem.getPath(), holder.imageView, new Point(imageSize, imageSize));

        holder.imageButton.setOnClickListener(v -> listener.onGetItem(currentItem));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageButton imageButton;

        public ImageViewHolder(ViewHolderListSelectedBinding binding) {
            super(binding.getRoot());
            imageButton = binding.buttonDeleteOnSelected;
            imageView = binding.imageView;
        }
    }
}