package com.example.mygallery.activities.imageViewActivity;

import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.viewmodel.FavoritesViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;

public class FavoritesViewActivity extends ImageViewActivity {

    @Override
    protected void initializeViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(FavoritesViewModel.class);
    }

    @Override
    protected void handleAddFavoritesClick() {
        ((FavoritesViewModel) viewModel).updateDatabase(currentPosition);
        getFavorite();
    }
}