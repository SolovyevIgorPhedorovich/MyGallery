package com.example.mygallery.editor;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.adapters.viewholder.ImageViewHolder;
import com.example.mygallery.databinding.ItemAppBinding;
import com.example.mygallery.interfaces.OnItemClickListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ResolveInfoAdapter extends RecyclerView.Adapter<ResolveInfoAdapter.ViewHolder> {

    private final List<ResolveInfo> resolveInfoList;
    private final PackageManager packageManager;
    private final OnItemClickListener listener;

    public ResolveInfoAdapter(List<ResolveInfo> resolveInfoList, PackageManager packageManager, OnItemClickListener listener) {
        this.resolveInfoList = resolveInfoList;
        this.packageManager = packageManager;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ResolveInfoAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemAppBinding binding = ItemAppBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ResolveInfoAdapter.ViewHolder holder, int position) {
        ResolveInfo resolveInfo = resolveInfoList.get(position);
        String appName = resolveInfo.loadLabel(packageManager).toString();
        Drawable appIcon = resolveInfo.loadIcon(packageManager);

        holder.appIcon.setImageDrawable(appIcon);
        holder.appName.setText(appName);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return resolveInfoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView appName;
        public ImageView appIcon;

        public ViewHolder(ItemAppBinding binding) {
            super(binding.getRoot());
            appName = binding.appName;
            appIcon = binding.appIcon;
        }
    }
}
