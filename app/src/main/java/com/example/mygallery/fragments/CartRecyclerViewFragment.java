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
            setEmptyCartText();
            showTextInFragment();
        }
    }

    private void initializeViewModel() {
        viewModel = createViewModel();
    }

    private CartViewModel createViewModel() {
        ViewModelProvider.Factory factory = ViewModelFactory.factory(this);
        return new ViewModelProvider(this, factory).get(CartViewModel.class);
    }

    private void setEmptyCartText() {
        setTextFragment(R.string.empty_cart);
    }
}