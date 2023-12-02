package com.example.mygallery.fragments.SelectBarFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import com.example.mygallery.R;
import com.example.mygallery.fragments.ActionFileFragment;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.managers.FileManager;
import com.example.mygallery.navigator.FragmentNavigator;
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
        button.setOnClickListener(view -> {
            if (buttonId == R.id.button_remove) {
                PopupWindowRemovedContextMenu.run(context, view, viewModel, this::closeFragment);
            } else if (buttonId == R.id.button_reset) {
                FileManager fileManager = new FileManager(context, viewModel, this::closeFragment);
                fileManager.resetFile();
            } else if (buttonId == R.id.button_selected_all) {
                if (!isAllSelected) {
                    viewModel.selectAll();
                } else {
                    viewModel.clearAll();
                }
            }
        });
    }
}
