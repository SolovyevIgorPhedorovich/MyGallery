package com.example.mygallery.activities.imageViewActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.databinding.ActivityImageInCartViewBinding;
import com.example.mygallery.managers.FileManager;
import com.example.mygallery.popupWindow.PopupWindowRemovedContextMenu;
import com.example.mygallery.viewmodel.CartViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;

public class CartViewActivity extends ViewActivity {
    private ImageButton buttonRemoveFile,
            buttonReset;
    private ActivityImageInCartViewBinding binding;
    private FileManager fileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageInCartViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        onCreate();

        this.fileManager = new FileManager(this, viewModel);
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

        buttonRemoveFile.setOnClickListener(v -> PopupWindowRemovedContextMenu.show(this, v, viewModel, initialPosition));

        buttonReset.setOnClickListener(v -> {
            fileManager.setPosition(initialPosition);
            fileManager.resetFile();
        });
    }
}