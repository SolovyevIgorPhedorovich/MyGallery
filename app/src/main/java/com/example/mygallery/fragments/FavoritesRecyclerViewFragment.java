package com.example.mygallery.fragments;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.R;
import com.example.mygallery.activities.imageViewActivity.FavoritesViewActivity;
import com.example.mygallery.viewmodel.FavoritesViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;
import org.jetbrains.annotations.NotNull;

public class FavoritesRecyclerViewFragment extends ImageGrid {

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        initializeViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getData();
    }

    @Override
    protected void viewFragmentText(Boolean isEmpty) {
        if (!isEmpty) {
            hideTextInFragment();
        } else {
            showEmptyFavoritesText();
        }
    }

    private void initializeViewModel() {
        viewModel = createViewModel();
    }

    private FavoritesViewModel createViewModel() {
        ViewModelProvider.Factory factory = ViewModelFactory.factory(this);
        return new ViewModelProvider(this, factory).get(FavoritesViewModel.class);
    }

    @Override
    protected void handleViewingMode(int position) {
        openActivity(position, FavoritesViewActivity.class);
    }

    private void showEmptyFavoritesText() {
        setTextFragment(R.string.empty_favorites);
        showTextInFragment();
    }
}
