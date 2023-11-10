package com.example.mygallery.fragments;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.R;
import com.example.mygallery.viewmodel.FavoritesViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;
import org.jetbrains.annotations.NotNull;

public class FavoritesRecyclerViewFragment extends ImageGrid {

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(FavoritesViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getData();
    }

    @Override
    protected void viewFragmentText(Boolean isEmpty) {
        if (!isEmpty)
            hideTextInFragment();
        else {
            setTextFragment(R.string.empty_favorites);
            showTextInFragment();
        }
    }

}
