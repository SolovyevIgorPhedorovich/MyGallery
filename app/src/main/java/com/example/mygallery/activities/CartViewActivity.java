package com.example.mygallery.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.databinding.ActivityImageInCartViewBinding;
import com.example.mygallery.models.Image;
import com.example.mygallery.popupWindow.PopupWindowActionFileContextMenu;
import com.example.mygallery.popupWindow.PopupWindowRemovedContextMenu;
import com.example.mygallery.viewmodel.CartViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;

public class CartViewActivity extends ViewActivity {
    private ImageButton buttonRemoveFile,
            buttonReset;
    private ActivityImageInCartViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityImageInCartViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        onCreate();
    }

    @Override
    protected void initializeViews() {
        super.initializeViews();

        buttonRemoveFile = binding.buttonRemove;
        buttonReset = binding.buttonReset;
    }

    @Override
    protected void initializeViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(CartViewModel.class);
    }


    //Обработка нажатий
    @Override
    protected void setOnClickListenerButtons() {
        super.setOnClickListenerButtons();

        buttonRemoveFile.setOnClickListener(v -> PopupWindowRemovedContextMenu.run(this, v, viewModel, initialPosition));

        buttonReset.setOnClickListener(v -> PopupWindowActionFileContextMenu.show(this, v, (Image) viewModel.getItem(initialPosition), viewModel, configurationViewPager));
    }
}