package com.example.mygallery.fragments;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.activities.imageViewActivity.ImageViewActivity;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.viewmodel.BaseViewModel;
import com.example.mygallery.viewmodel.ImageViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ImageRecyclerViewFragment extends ImageGrid {
    private final File albumPath;

    public ImageRecyclerViewFragment(File albumPath) {
        this.albumPath = albumPath;
    }

    public ImageRecyclerViewFragment(File albumPath, BaseViewModel<Model> viewModel) {
        this(albumPath);
        this.viewModel = viewModel;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        initializeViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (viewModel instanceof ImageViewModel && viewModel.isEmpty()) {
            ((ImageViewModel) viewModel).scanMediaAlbum(albumPath);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.clear();
    }

    @Override
    protected void handleViewingMode(int position) {
        openActivity(position, ImageViewActivity.class);
    }

    private void initializeViewModel() {
        if (viewModel == null) {
            viewModel = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(ImageViewModel.class);
        }
    }
}