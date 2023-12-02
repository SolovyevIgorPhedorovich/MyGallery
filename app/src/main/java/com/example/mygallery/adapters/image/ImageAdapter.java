package com.example.mygallery.adapters.image;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.example.mygallery.ImageSizeCalculator;
import com.example.mygallery.adapters.viewholder.ImageViewHolder;
import com.example.mygallery.databinding.ItemGalleryBinding;
import com.example.mygallery.interfaces.OnCheckedIsChoice;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.interfaces.model.Model;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ImageAdapter<T> extends ImageAdapterHelper<T> {
    private final Context context;
    private final int spanCount;
    private Point imageSize;

    public ImageAdapter(Context context, List<T> dataList, int spanCount, OnItemClickListener listener, OnItemClickListener selectClickListener, OnItemClickListener longClickListener, OnCheckedIsChoice checked) {
        super(dataList, listener, selectClickListener, longClickListener, checked);
        this.context = context;
        this.spanCount = spanCount;
        mode = Mode.VIEWING;
    }

    private void calculatedImageSize(View view) {
        ImageSizeCalculator imageSizeCalculator = new ImageSizeCalculator(context, view, spanCount);
        imageSizeCalculator.calculateSquareShareSize();
        this.imageSize = imageSizeCalculator.getResult();
    }

    private void setLayoutParams(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = imageSize.x;
        layoutParams.height = imageSize.y;
        view.setLayoutParams(layoutParams);
    }

    @NonNull
    @NotNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemGalleryBinding binding = ItemGalleryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        View itemView = binding.getRoot();
        calculatedImageSize(itemView);
        setLayoutParams(itemView);
        return new ImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        setMode(holder, position);
        setImage(((Model) dataList.get(position)).getPath(), holder.imageView, imageSize);

        if (mode == Mode.SELECTED) {
            updateSelectedItems(position);
            setCheckedState(holder, position);
        }

    }
}