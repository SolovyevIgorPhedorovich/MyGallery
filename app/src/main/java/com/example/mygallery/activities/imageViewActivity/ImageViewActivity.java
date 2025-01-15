package com.example.mygallery.activities.imageViewActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.R;
import com.example.mygallery.databinding.ActivityImageViewBinding;
import com.example.mygallery.editor.ImageEditor;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Image;
import com.example.mygallery.popupWindow.PopupWindowActionFileContextMenu;
import com.example.mygallery.popupWindow.PopupWindowRemovedContextMenu;
import com.example.mygallery.share.ShareFile;
import com.example.mygallery.viewmodel.ImageViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;

public class ImageViewActivity extends ViewActivity {
    private ImageButton buttonRemoveFile, buttonContextMenu, buttonAddFavorites, buttonEdit, buttonShare;
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
        buttonShare = binding.buttonShare;
    }

    @Override
    protected void initializeViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(ImageViewModel.class);
    }

    @Override
    protected void setOnClickListenerButtons() {
        super.setOnClickListenerButtons();
        setButtonShareClickListener();
        setButtonEditClickListener();
        setButtonRemoveFileClickListener();
        setButtonAddFavoritesClickListener();
        setButtonContextMenuClickListener();
    }

    private void setButtonShareClickListener(){
        buttonShare.setOnClickListener(v -> {
            ShareFile shareFile = new ShareFile(this);
            shareFile.shareFile(getFileUri());
        });
    }

    private void setButtonEditClickListener() {
        buttonEdit.setOnClickListener(v -> new ImageEditor(this, v, getFileUri()));
    }

    private void setButtonRemoveFileClickListener() {
        buttonRemoveFile.setOnClickListener(v -> PopupWindowRemovedContextMenu.show(this, v, viewModel, currentPosition));
    }

    private void setButtonAddFavoritesClickListener() {
        buttonAddFavorites.setOnClickListener(v -> {
            handleAddFavoritesClick();
            updateButtonFavorite();
        });
    }

    private void setButtonContextMenuClickListener() {
        buttonContextMenu.setOnClickListener(v -> PopupWindowActionFileContextMenu.show(this, v, getCurrentItem(), viewModel, configurationViewPager));
    }

    private Uri getFileUri() {
        return FileProvider.getUriForFile(this, "com.example.mygallery.fileprovider", viewModel.getItem(currentPosition).getPath());
    }

    protected void handleAddFavoritesClick() {
        ((ImageViewModel) viewModel).setFavorites(viewModel.getItem(currentPosition));
        getFavorite();
    }

    protected void getFavorite() {
        isFavorite = ((Image) viewModel.getItem(currentPosition)).isFavorite;
    }

    protected void updateButtonFavorite() {
        int resource = isFavorite ? R.drawable.favorite_selected_32_regular : R.drawable.favorites_32_regular;
        buttonAddFavorites.setImageResource(resource);
    }

    @Override
    protected void setNameItem(int position) {
        super.setNameItem(position);
        isFavorite = ((Image) viewModel.getItem(position)).isFavorite;
        updateButtonFavorite();
    }

    private Model getCurrentItem() {
        return viewModel.getItem(currentPosition);
    }
}