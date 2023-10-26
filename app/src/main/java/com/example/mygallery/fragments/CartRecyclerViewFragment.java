package com.example.mygallery.fragments;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.mygallery.R;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.adapters.image.ImageAdapter;
import com.example.mygallery.adapters.image.ImageAdapterHelper;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Cart;
import com.example.mygallery.viewmodel.BaseViewModel;
import com.example.mygallery.viewmodel.CartViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;
import org.jetbrains.annotations.NotNull;

public class CartRecyclerViewFragment extends RecyclerViewFragment {
    private CartViewModel cart;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        cart = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(CartViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        cart.getData();
    }

    @Override
    protected void setObserve() {
        cart.data.observe(getViewLifecycleOwner(), o -> {
            ImageAdapterHelper<Model> adapter = (ImageAdapterHelper<Model>) recyclerView.getAdapter();
            if (adapter != null) {
                Log.d("Update", "Данные обновлены");
                adapter.setDataList(o);
                adapter.notifyDataSetChanged();
                viewFragmentText(o.isEmpty());
            }
        });
    }

    public void updateData() {
    }

    @Override
    protected void configureRecyclerView() {
        updateData();
        createAdapter(); // Создание адаптера для корзины
        recyclerView.setLayoutManager(new GridLayoutManager(fragmentContext, 4));
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

    protected void createAdapter() {
        ImageAdapter<Model> adapter = new ImageAdapter<Model>(fragmentContext, cart.getList(), p -> openActivity(p, ImageViewActivity.class), null, null);
        recyclerView.setAdapter(adapter);
    }


}
