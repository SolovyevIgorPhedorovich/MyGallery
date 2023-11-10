package com.example.mygallery.fragments;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.R;
import com.example.mygallery.viewmodel.CartViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;
import org.jetbrains.annotations.NotNull;

public class CartRecyclerViewFragment extends ImageGrid {

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(CartViewModel.class);
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
            setTextFragment(R.string.empty_cart);
            showTextInFragment();
        }
    }


}
