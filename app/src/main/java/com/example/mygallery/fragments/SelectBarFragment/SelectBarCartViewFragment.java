package com.example.mygallery.fragments.SelectBarFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import com.example.mygallery.R;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.managers.FileManager;
import com.example.mygallery.popupWindow.PopupWindowRemovedContextMenu;
import com.example.mygallery.viewmodel.BaseViewModel;

public class SelectBarCartViewFragment extends SelectBarFragment {

    public SelectBarCartViewFragment(BaseViewModel<Model> viewModel, Fragment parentFragment) {
        super(viewModel, parentFragment);
    }

    @Override
    protected View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_cart_select_bar, container, false);
    }

    @Override
    protected void setButtonClickListener(int buttonId, ImageButton button) {
        button.setOnClickListener(view -> handleButtonClick(buttonId));
    }

    private void handleButtonClick(int buttonId) {
        if (buttonId == R.id.button_remove) {
            handleRemoveButtonClick();
        } else if (buttonId == R.id.button_reset) {
            handleResetButtonClick();
        } else if (buttonId == R.id.button_selected_all) {
            handleSelectedAllButtonClick();
        }
    }

    //Удаление файлов из корзины
    private void handleRemoveButtonClick() {
        PopupWindowRemovedContextMenu.show(context, buttonToolbar.get(R.id.button_remove), viewModel, this::closeFragment);
    }

    //Востановление файлов из корзины
    private void handleResetButtonClick() {
        FileManager fileManager = new FileManager(context, viewModel, this::closeFragment);
        fileManager.resetFile();
    }
}