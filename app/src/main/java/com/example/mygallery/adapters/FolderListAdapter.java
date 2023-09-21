package com.example.mygallery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mygallery.R;
import com.example.mygallery.activities.AlbumActivity;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.managers.DataManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.FolderViewHolder> {

    private final Context context;
    private final List<String> folderNames;
    private final List<String> folderCovers;
    private final List<Integer> fileCounts;
    private OnItemClickListener listener;
    private OnFolderItemClickListener itemClickListener;
    // Конструктор класса
    public FolderListAdapter(Context context) {
        this.context = context;
        DataManager dataManager = DataManager.getInstance(context);
        this.folderNames = new ArrayList<>(dataManager.getFolderNamesList());
        this.folderCovers = new ArrayList<>(dataManager.getFolderCoversList());
        this.fileCounts = new ArrayList<>(dataManager.getFileCountList());
        removeInitialFolder();
    }

    public FolderListAdapter(Context context, OnItemClickListener listener) {
        this(context);
        this.listener = listener;
    }

    // Устанавливаем слушатель для кликов
    public void setOnFolderItemClickListener(OnFolderItemClickListener listener) {
        this.itemClickListener = listener;
    }

    // Удаляем начальную папку, если активити - ImageViewActivity
    private void removeInitialFolder() {
        if (context instanceof ImageViewActivity) {
            ImageViewActivity imageViewActivity = (ImageViewActivity) context;
            int positionToRemove = imageViewActivity.getPosition();

            folderNames.remove(positionToRemove);
            folderCovers.remove(positionToRemove);
            fileCounts.remove(positionToRemove);
        }
    }

    // Создаем новый ViewHolder
    @NotNull
    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_object, parent, false);
        return new FolderViewHolder(itemView);
    }

    // Привязываем данные к ViewHolder
    @Override
    public void onBindViewHolder(final FolderViewHolder holder, int position) {
        loadFolderImage(holder.folderImageView, position);

        holder.folderNameTextView.setText(folderNames.get(position));
        holder.fileCountTextView.setText(String.valueOf(fileCounts.get(position)));

        if (context instanceof ImageViewActivity) {
            // Обработка клика на элементе
            holder.itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onFolderClick(folderNames.get(position));
                }
            });
        } else if (context instanceof AlbumActivity) {
            holder.itemView.setOnClickListener(v -> listener.onItemClick(folderNames.get(position), position));
        }
    }

    // Возвращает общее количество элементов в списке
    @Override
    public int getItemCount() {
        return folderNames.size();
    }

    // Загружаем изображение папки с использованием библиотеки Glide
    private void loadFolderImage(ImageView imageView, int position) {
        Glide.with(context)
                .load(folderCovers.get(position))
                .error(R.drawable.invalid_image)
                .into(imageView);
    }

    // Интерфейс для обработки кликов по элементам списка
    public interface OnFolderItemClickListener {
        void onFolderClick(String folderName);
    }

    // ViewHolder для элемента списка
    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView folderImageView;
        TextView folderNameTextView;
        TextView fileCountTextView;

        // Конструктор ViewHolder
        public FolderViewHolder(View itemView) {
            super(itemView);
            folderImageView = itemView.findViewById(R.id.album_artwork);
            fileCountTextView = itemView.findViewById(R.id.count_images);
            folderNameTextView = itemView.findViewById(R.id.folder_name);
        }
    }
}