package com.example.mygallery.activities.imageViewActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.R;
import com.example.mygallery.databinding.ActivityImageViewBinding;
import com.example.mygallery.editor.ImageEditor;
import com.example.mygallery.models.Image;
import com.example.mygallery.popupWindow.PopupWindowActionFileContextMenu;
import com.example.mygallery.popupWindow.PopupWindowRemovedContextMenu;
import com.example.mygallery.viewmodel.FavoritesViewModel;
import com.example.mygallery.viewmodel.ImageViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;

public class ImageViewActivity extends ViewActivity {
    private ImageButton buttonRemoveFile,
            buttonContextMenu,
            buttonAddFavorites,
            buttonEdit;
    private ActivityImageViewBinding binding;

    protected boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityImageViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        onCreate();
    }

    @Override
    protected void initializeViews() {
        super.initializeViews();
        buttonRemoveFile = binding.buttonRemove;
        buttonContextMenu = binding.buttonContextMenu;
        buttonAddFavorites = binding.buttonAddFavorites;
        buttonEdit = binding.buttonEdit;
    }

    @Override
    protected void initializeViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(ImageViewModel.class);
    }

    @Override
    protected void setOnClickListenerButtons() {
        super.setOnClickListenerButtons();

        buttonEdit.setOnClickListener(v -> new ImageEditor(this, v, FileProvider.getUriForFile(this, "com.example.mygallery.fileprovider", viewModel.getItem(initialPosition).getPath())));

        buttonRemoveFile.setOnClickListener(v -> PopupWindowRemovedContextMenu.run(this, v, viewModel, initialPosition));

        buttonAddFavorites.setOnClickListener(v -> {
            if (viewModel instanceof ImageViewModel) {
                ((ImageViewModel) viewModel).setFavorites(viewModel.getItem(initialPosition));
            } else if (viewModel instanceof FavoritesViewModel) {
                ((FavoritesViewModel) viewModel).updateDatabase(initialPosition);
            }
            isFavorite = ((Image) viewModel.getItem(initialPosition)).isFavorite;
            updateButtonFavorite();
        });

        buttonContextMenu.setOnClickListener(v -> PopupWindowActionFileContextMenu.show(this, v, (Image) viewModel.getItem(initialPosition), viewModel, configurationViewPager));
    }

    protected void updateButtonFavorite() {
        if (isFavorite) {
            buttonAddFavorites.setImageResource(R.drawable.favorite_selected_32_regular);
        } else {
            buttonAddFavorites.setImageResource(R.drawable.favorites_32_regular);
        }
    }

    @Override
    protected void setNameItem(int position) {
        super.setNameItem(position);
        isFavorite = ((Image) viewModel.getItem(position)).isFavorite;
        updateButtonFavorite();
    }
}